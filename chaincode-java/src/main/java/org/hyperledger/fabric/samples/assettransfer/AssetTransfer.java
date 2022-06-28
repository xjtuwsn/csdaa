/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


//import com.owlike.genson.annotation.JsonProperty;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.parameters.PropertiesParameters;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import com.owlike.genson.Genson;

@Contract(
        name = "basic",
        info = @Info(
                title = "Asset Transfer",
                description = "The hyperlegendary asset transfer",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "a.transfer@example.com",
                        name = "Adrian Transfer",
                        url = "https://hyperledger.example.com")))
@Default
public final class AssetTransfer implements ContractInterface {

    private final Genson genson = new Genson();

    private enum AssetTransferErrors {
        ASSET_NOT_FOUND,
        ASSET_ALREADY_EXISTS
    }

    /**
     * Creates some initial assets on the ledger.
     *
     * @param ctx the transaction context
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void InitLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        CreateAuditAsset(ctx, "asset1", "blue", "adsd", "Tomoko", "300", "2", "", "", "", 2, "", "");


    }


    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public AuditAssert CreateAuditAsset(final Context ctx, final String id, final String t, final String auditor,
                                   final String auditee, final String logType, final String z,
                                   final String r1, final String r2, final String proof,
                                   final int result, final String sigma, final String rightsum) {
        ChaincodeStub stub = ctx.getStub();

        if (AssetExists(ctx, id)) {
            String errorMessage = String.format("Asset %s already exists", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_ALREADY_EXISTS.toString());
        }

        if (logType.equals("result")) {
            String mysysparam = stub.getStringState("mysysparam");
            SystemParamter sp = genson.deserialize(mysysparam, SystemParamter.class);
            PropertiesParameters parameters = new PropertiesParameters();
            parameters.put("type", sp.getType());
            parameters.put("q", sp.getQ());
            parameters.put("r", sp.getR());
            parameters.put("h", sp.getH());
            parameters.put("exp1", sp.getExp1());
            parameters.put("exp2", sp.getExp2());
            parameters.put("sign0", sp.getSign0());
            parameters.put("sign1", sp.getSign1());
            Pairing pr = PairingFactory.getPairing(parameters);
            Field zr = pr.getZr();
            Field g1 = pr.getG1();
            Element g = g1.newElementFromBytes(Base64.getDecoder().decode(sp.getG()));
            Element u = g1.newElementFromBytes(Base64.getDecoder().decode(sp.getU()));
            Element proofe = zr.newElementFromBytes(Base64.getDecoder().decode(proof));
            Element leftsum = g1.newElementFromBytes(Base64.getDecoder().decode(sigma));
            Element rightsume = g1.newElementFromBytes(Base64.getDecoder().decode(rightsum));
            Element left = pr.pairing(leftsum, g);
            Element right = pr.pairing(rightsume.mul(u.duplicate().powZn(proofe)),
                    g1.newElementFromBytes(Base64.getDecoder().decode(t)));
            AuditAssert asset = null;
            if (left.isEqual(right) && result == 1) {
                asset = new AuditAssert(id, t, auditor, auditee, logType, z, r1, r2, proof, 1, sigma);
            } else {
                asset = new AuditAssert(id, t, auditor, auditee, logType, z, r1, r2, proof, 0, sigma);
            }

            String sortedJson = genson.serialize(asset);
            stub.putStringState(id, sortedJson);

            return asset;
        }
        if (logType.equals("batch_result")) {
            String mysysparam = stub.getStringState("mysysparam");
            SystemParamter sp = genson.deserialize(mysysparam, SystemParamter.class);
            PropertiesParameters parameters = new PropertiesParameters();
            parameters.put("type", sp.getType());
            parameters.put("q", sp.getQ());
            parameters.put("r", sp.getR());
            parameters.put("h", sp.getH());
            parameters.put("exp1", sp.getExp1());
            parameters.put("exp2", sp.getExp2());
            parameters.put("sign0", sp.getSign0());
            parameters.put("sign1", sp.getSign1());
            Pairing pr = PairingFactory.getPairing(parameters);
            Field zr = pr.getZr();
            Field g1 = pr.getG1();
            Element g = g1.newElementFromBytes(Base64.getDecoder().decode(sp.getG()));
            Element u = g1.newElementFromBytes(Base64.getDecoder().decode(sp.getU()));
            String[] proofs = proof.split("<#>");
            String[] rightSums = rightsum.split("<#>");
            Element right = null;
            for (int i = 0; i < proofs.length; i++) {
                Element proofe = zr.newElementFromBytes(Base64.getDecoder().decode(proofs[i]));
                Element rightsume = g1.newElementFromBytes(Base64.getDecoder().decode(rightSums[i]));
                Element tmp = pr.pairing(rightsume.mul(u.duplicate().powZn(proofe)),
                        g1.newElementFromBytes(Base64.getDecoder().decode(t)));
                right = (right == null ? tmp : right.duplicate().mul(tmp));
            }
            Element leftsum = g1.newElementFromBytes(Base64.getDecoder().decode(sigma));
            Element left = pr.pairing(leftsum, g);
            AuditAssert asset = null;
            if (left.isEqual(right) && result == 1) {
                asset = new AuditAssert(id, t, auditor, auditee, logType, z, r1, r2, proof, 1, sigma);
            } else {
                asset = new AuditAssert(id, t, auditor, auditee, logType, z, r1, r2, proof, 0, sigma);
            }

            String sortedJson = genson.serialize(asset);
            stub.putStringState(id, sortedJson);

            return asset;
        }
        AuditAssert asset = new AuditAssert(id, t, auditor, auditee, logType, z, r1, r2, proof, result, sigma);
        //Use Genson to convert the Asset into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(asset);
        stub.putStringState(id, sortedJson);

        return asset;
    }
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public FileIndex CreateFileAsset(final Context ctx, final String id, final String t, final String user, final String ssp) {
        ChaincodeStub stub = ctx.getStub();
        if (AssetExists(ctx, id)) {
            String errorMessage = String.format("Asset %s already exists", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_ALREADY_EXISTS.toString());
        }
        FileIndex file = new FileIndex(t, user, ssp);
        String serialize = genson.serialize(file);
        stub.putStringState(id, serialize);
        return file;
    }
    /**
     * Retrieves an asset with the specified ID from the ledger.
     *
     * @param ctx the transaction context
     * @param assetID the ID of the asset
     * @return the asset found on the ledger if there was one
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public AuditAssert ReadAuditAsset(final Context ctx, final String assetID) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(assetID);

        if (assetJSON == null || assetJSON.isEmpty()) {
            String errorMessage = String.format("Asset %s does not exist", assetID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }

        AuditAssert asset = genson.deserialize(assetJSON, AuditAssert.class);
        return asset;
    }
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public FileIndex ReadFileAsset(final Context ctx, final String id) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(id);
        if (assetJSON == null || assetJSON.isEmpty()) {
            String errorMessage = String.format("Asset %s does not exist", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }
        FileIndex deserialize = genson.deserialize(assetJSON, FileIndex.class);
        return deserialize;
    }
    /**
     * Updates the properties of an asset on the ledger.
     *
     * @param ctx the transaction context
     * @param assetID the ID of the asset being updated
     * @param color the color of the asset being updated
     * @param size the size of the asset being updated
     * @param owner the owner of the asset being updated
     * @param appraisedValue the appraisedValue of the asset being updated
     * @return the transferred asset
     */
//    @Transaction(intent = Transaction.TYPE.SUBMIT)
//    public Asset UpdateAsset(final Context ctx, final String assetID, final String color, final int size,
//        final String owner, final int appraisedValue) {
//        ChaincodeStub stub = ctx.getStub();
//
//        if (!AssetExists(ctx, assetID)) {
//            String errorMessage = String.format("Asset %s does not exist", assetID);
//            System.out.println(errorMessage);
//            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
//        }
//
//        Asset newAsset = new Asset(assetID, color, size, owner, appraisedValue);
//        //Use Genson to convert the Asset into string, sort it alphabetically and serialize it into a json string
//        String sortedJson = genson.serialize(newAsset);
//        stub.putStringState(assetID, sortedJson);
//        return newAsset;
//    }

    /**
     * Deletes asset on the ledger.
     *
     * @param ctx the transaction context
     * @param assetID the ID of the asset being deleted
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void DeleteAsset(final Context ctx, final String assetID) {
        ChaincodeStub stub = ctx.getStub();

        if (!AssetExists(ctx, assetID)) {
            String errorMessage = String.format("Asset %s does not exist", assetID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }

        stub.delState(assetID);
    }
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void SetSystemParamter(final Context ctx, final String type, final String q, final String r, final String h,
                                  final String exp1, final String exp2, final String sign0, final String sign1, final String g,
                                  final String u, final String hash1, final String hash2) {
        String id = "mysysparam";
        ChaincodeStub stub = ctx.getStub();
        if (AssetExists(ctx, id)) {
            stub.delState(id);
        }
        SystemParamter systemParamter = new SystemParamter(type, q, r, h, exp1, exp2, sign0, sign1, g, u, hash1, hash2);
        String serialize = genson.serialize(systemParamter);
        stub.putStringState(id, serialize);
    }
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public SystemParamter GetSystemParam(final Context ctx) {
        String assetID = "mysysparam";
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(assetID);
        if (assetJSON == null || assetJSON.isEmpty()) {
            String errorMessage = String.format("Asset %s does not exist", assetID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }
        SystemParamter deserialize = genson.deserialize(assetJSON, SystemParamter.class);
        return deserialize;
    }
    /**
     * Checks the existence of the asset on the ledger
     *
     * @param ctx the transaction context
     * @param assetID the ID of the asset
     * @return boolean indicating the existence of the asset
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean AssetExists(final Context ctx, final String assetID) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(assetID);

        return (assetJSON != null && !assetJSON.isEmpty());
    }

    /**
     * Changes the owner of a asset on the ledger.
     *
     * @param ctx the transaction context
     * @param assetID the ID of the asset being transferred
     * @param newOwner the new owner
     * @return the old owner
     */
//    @Transaction(intent = Transaction.TYPE.SUBMIT)
//    public String TransferAsset(final Context ctx, final String assetID, final String newOwner) {
//        ChaincodeStub stub = ctx.getStub();
//        String assetJSON = stub.getStringState(assetID);
//
//        if (assetJSON == null || assetJSON.isEmpty()) {
//            String errorMessage = String.format("Asset %s does not exist", assetID);
//            System.out.println(errorMessage);
//            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
//        }
//
//        Asset asset = genson.deserialize(assetJSON, Asset.class);
//
//        Asset newAsset = new Asset(asset.getAssetID(), asset.getColor(), asset.getSize(), newOwner, asset.getAppraisedValue());
//        //Use a Genson to conver the Asset into string, sort it alphabetically and serialize it into a json string
//        String sortedJson = genson.serialize(newAsset);
//        stub.putStringState(assetID, sortedJson);
//
//        return asset.getOwner();
//    }

    /**
     * Retrieves all assets from the ledger.
     *
     * @param ctx the transaction context
     * @return array of assets found on the ledger
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllAssets(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        List<AuditAssert> queryResults = new ArrayList<AuditAssert>();

        // To retrieve all assets from the ledger use getStateByRange with empty startKey & endKey.
        // Giving empty startKey & endKey is interpreted as all the keys from beginning to end.
        // As another example, if you use startKey = 'asset0', endKey = 'asset9' ,
        // then getStateByRange will retrieve asset with keys between asset0 (inclusive) and asset9 (exclusive) in lexical order.
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result: results) {
            AuditAssert asset = genson.deserialize(result.getStringValue(), AuditAssert.class);
            System.out.println(asset);
            queryResults.add(asset);
        }

        final String response = genson.serialize(queryResults);

        return response;
    }
}
