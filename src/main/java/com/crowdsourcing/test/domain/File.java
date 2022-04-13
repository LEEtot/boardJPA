package com.crowdsourcing.test.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fileId")
    private Long id;

    @Column(nullable = false)
    private String originFileName;
    @Column(nullable = false)
    private String fileName;
    @Column(nullable = false)
    private String filePath;
    @Column(nullable = false)
    private String useFile;

    @ManyToOne
    @JoinColumn(name = "fileMasterId")
    private FileMaster fileMaster;

    public void addFileMaster(FileMaster fileMaster) {
        this.fileMaster = fileMaster;
        fileMaster.getFileList().add(this);
    }
}
