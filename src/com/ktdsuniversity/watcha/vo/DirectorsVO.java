package com.ktdsuniversity.watcha.vo;

/**
 * VO: Value Object 데이터(멤버변수와 겟터/셋터)만 가지고 있는 클래스
 */
public class DirectorsVO {

	private String directorId;
	private String name;
	private String profile;

	public String getDirectorId() {
		return directorId;
	}

	public void setDirectorId(String directorId) {
		this.directorId = directorId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	// getter / setter
	// alt + shift + s -> r -> Alt + a -> Alt + r

}
