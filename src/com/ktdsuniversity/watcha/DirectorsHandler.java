package com.ktdsuniversity.watcha;

import com.ktdsuniversity.watcha.service.DirectorsService;

public class DirectorsHandler {

	public static void main(String[] args) {
		
		DirectorsService directorsService = new DirectorsService();
		boolean wasCreate = directorsService.createNewDirector("장항준", "장항준.png");
		
		if (wasCreate) {
			System.out.println("감독을 잘 생성했습니다.");
		}
		else {
			System.out.println("감독 생성에 실패했습니다.");
		}
		
	}
	
}
