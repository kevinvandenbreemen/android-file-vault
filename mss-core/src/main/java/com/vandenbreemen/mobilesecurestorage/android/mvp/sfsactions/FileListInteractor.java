package com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions;

import com.vandenbreemen.mobilesecurestorage.file.api.FileType;
import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes;
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kevin
 */
public class FileListInteractor {

    private SecureFileSystem sfs;

    FileListInteractor(SecureFileSystem sfs) {
        this.sfs = sfs;
    }

    public List<FileListItemView> getFileList() {
        return sfs.listFiles().stream().map((item) -> {

            FileType fileType = FileTypes.UNKNOWN;
            try {
                fileType = sfs.getDetails(item).getFileType();
            } catch (Exception cex) {

            }

            return new FileListItemView(item,
                    fileType
            );
        }).collect(Collectors.toList());
    }

}
