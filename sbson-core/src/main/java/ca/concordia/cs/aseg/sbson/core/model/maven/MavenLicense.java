package ca.concordia.cs.aseg.sbson.core.model.maven;

public class MavenLicense {

	private String name, url, comment, distribution;

	public MavenLicense() {
	
	}
	
	
	public MavenLicense(String name, String url, String comment, String distribution) {
		super();
		this.name = name;
		this.url = url;
		this.comment = comment;
		this.distribution = distribution;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDistribution() {
		return distribution;
	}

	public void setDistribution(String distribution) {
		this.distribution = distribution;
	}
	
}
