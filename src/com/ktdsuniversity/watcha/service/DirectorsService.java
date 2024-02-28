package com.ktdsuniversity.watcha.service;

import com.ktdsuniversity.watcha.dao.DirectorsDao;
import com.ktdsuniversity.watcha.util.DBSupporter;
import com.ktdsuniversity.watcha.vo.DirectorsVO;

/**
 * DB의 트랜잭션을 관리.
 */
public class DirectorsService {

	private DirectorsDao directorsDao;
	
	public DirectorsService() {
		this.directorsDao = new DirectorsDao();
	}
	
	public boolean createNewDirector(String directorsName, String directorsProfile) {
		DBSupporter dbSupporter = new DBSupporter("WATCHA", "WATCHA");
		dbSupporter.open(); // Database에 연결하는 역할. Autocommit 비활성화 처리.
		
		DirectorsVO directorsVO = new DirectorsVO();
		directorsVO.setName(directorsName);
		directorsVO.setProfile(directorsProfile);
		
		// insert 처리중에 예외가 발생한다면, 변경사항들을 모두 ROLLBACK 한다.
		int insertedCount = 0;
		try {
			insertedCount = this.directorsDao.insertNewDirector(dbSupporter, directorsVO);
		}
		catch( RuntimeException re ) {
			re.printStackTrace();
			dbSupporter.rollback();
		}
		
		dbSupporter.close(); // Database와 연결을 해제한다. 별다른 문제가 없었을 때는 Commit을 처리한다.
		return insertedCount > 0;
	}
	
}










