'use strict';

const { WorkloadModuleBase } = require('@hyperledger/caliper-core');




class CreateSubsidyWorkload extends WorkloadModuleBase {

    constructor() {
        super();
        this.txIndex = 0;
    }
    async initializeWorkloadModule(workerIndex, totalWorkers, roundIndex, roundArguments, sutAdapter, sutContext) {
        await super.initializeWorkloadModule(workerIndex, totalWorkers, roundIndex, roundArguments, sutAdapter, sutContext);
    }

    async submitTransaction() {
        this.txIndex++;
        new Date().getTime().toString();

        const myArgs = {
            contractId: this.roundArguments.contractId,
            contractFunction: 'CreateAuditAsset',
            invokerIdentity: 'Admin@org1.example.com',
            contractArguments: [new Date().getTime().toString()+"_"+this.txIndex,'aaa','vvv','bbb','acx',1,'r1','r2','proof',0,'a','vb'],
            readOnly: false
        };


        await this.sutAdapter.sendRequests(myArgs);
    }
}

function createWorkloadModule() {
    return new CreateSubsidyWorkload();
}
module.exports.createWorkloadModule = createWorkloadModule;
