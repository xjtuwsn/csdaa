package com.csdaa.wsn.server.mapper;

import com.csdaa.wsn.commons.entity.FileBlock;
import com.csdaa.wsn.commons.entity.TagAndCK;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface DepMapper {

    Integer isTExist(String tag);
    Integer storeBlockDetail(FileBlock fileBlock);
    Integer isFileExist(String t);
    void insertFile(String name,String flag);
    String getKesi(String flag,int index);
    String getTag(String flag,int index);
    void addOwner(String t,String user);
    void stroeFileDetail(String flag,int block,String tag,String ck,String sigima);
    Integer isOwner(String t,String user);
    TagAndCK getTagCk(String t, int block);
    void deleteOwner(String t,String user);
    void deleteFile(String t);
    Integer hasOwner(String t);
    Integer getFileId(String t);
    List<Integer> getNoUseTag(int fileID);
    void deleteFileDetail(int fileID);
    void deleteBlock(int bid);
    String getTagById(int bid);
}
