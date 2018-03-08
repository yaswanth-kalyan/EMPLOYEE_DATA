package models.wiki;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.avaje.ebean.Model;

import models.BaseEntity;

@Entity
public class Page extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	@NotNull
	@NotBlank
	public String title;

	@OneToMany
	public List<PageHistory> pageHistoryList = new ArrayList<>();

	public static Model.Finder<Long, Page> find = new Model.Finder<Long, Page>(Page.class);

	public PageHistory getLastHistory() {

		return pageHistoryList.stream().sorted((h1, h2) -> Integer.compare(h2.version, h1.version)).findFirst().get();

	}

	public Boolean isActive = Boolean.TRUE;
}
