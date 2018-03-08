package models.lead;

import java.util.Arrays;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import com.avaje.ebean.Model;

@Entity
public class StoreFile extends Model{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@Lob
	public byte[] file; 

	public String fileName ;
	public String contentType ; 
	
	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public static Model.Finder<Long, StoreFile> find = new Model.Finder<Long, StoreFile>(StoreFile.class);

	@Override
	public String toString() {
		return "StoreFile [id=" + id + ", file=" + Arrays.toString(file) + "]";
	}
	
	
}
