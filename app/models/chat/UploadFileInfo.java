package models.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import models.AppUser;
import models.BaseEntity;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class UploadFileInfo extends BaseEntity{
	    @Id
	    public Long id;
	    
	    @Lob
	    @JsonIgnore
	    public byte[] uploadImage;
	    
	    public String uploadFileContentType ;
	    
	    public String uploadFileName ;
	    
	    @JsonIgnore
	    public String fileSize;
	    
	    @Lob
	    @JsonIgnore
	    public byte[] reSizeImage;
	    
	    @JsonIgnore
	    public String fileUrl;
	    
	    @ManyToOne
	    @JoinColumn(nullable=false,unique=false)
	    @JsonIgnore
	    public AppUser appUser;
	    
	    @ManyToOne(cascade=CascadeType.REMOVE)
	    @JoinColumn(nullable=true,unique=false)
	    @JsonIgnore
	    public GitIssue gitIssue;
	    
	    
	    @JsonIgnore
	    @OneToOne
	    public Message message;
	    
	    @OneToMany(mappedBy="uploadFileInfo",cascade=CascadeType.ALL)
	    public List<FileComment> commentList=new ArrayList<FileComment>();
	    

	    @OneToMany(mappedBy="uploadFileInfo",cascade=CascadeType.ALL)
	    @JsonIgnore
	    public List<FileLike> likeList=new ArrayList<FileLike>();
	    
	    @Transient
	    public Map<Object,Object> snippetMap=new HashMap<Object,Object>();
	    
	    
	    
	    
	    
	    
	    @Override
		public String toString() {
			return "UploadFileInfo [id=" + id + ","
					+ ", uploadFileContentType=" + uploadFileContentType + ", uploadFileName=" + uploadFileName
					+ ", fileUrl=" + fileUrl + ", appUser=" + appUser + ", gitIssue=" + gitIssue + ", message="
					+ message + ", commentList=" + commentList + ", snippetMap=" + snippetMap + "]";
		}






		public static Model.Finder<Long,UploadFileInfo> find = new Model.Finder<Long,UploadFileInfo>(UploadFileInfo.class);
	    
	    

}
