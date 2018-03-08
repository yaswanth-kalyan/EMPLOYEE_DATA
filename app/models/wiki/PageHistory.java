package models.wiki;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import com.avaje.ebean.Model;

import models.AppUser;
import models.BaseEntity;

@Entity
public class PageHistory extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	public Integer version = 0;

	@Column(columnDefinition = "text")
	public String content;

	@ManyToOne
	public Page page;

	@ManyToOne
	public AppUser appUser;

	public static Model.Finder<Long, PageHistory> find = new Model.Finder<Long, PageHistory>(PageHistory.class);

	public String getHtml() {
		Parser parser = Parser.builder().build();
		Node document = parser.parse(this.content);
		HtmlRenderer renderer = HtmlRenderer.builder().build();
		return renderer.render(document);
	}
	
	public Integer getVersion(){
		return this.version;
	}
}
