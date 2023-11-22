package com.gdu.petmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.petmall.dto.QattachDto;
import com.gdu.petmall.dto.QnaDto;

@Mapper
public interface QnaMapper {
   
   // 게시글 추가 
   public int insertQna(QnaDto qna);
   
   // 게시글 첨부파일 추가
   public int insertQattach(QattachDto qattach);
   
   // 게시글 전체 목록
   public List<QnaDto> getQnaList(Map<String, Object> map);
   
   // 게시글 상세보기 목록
	public List<QnaDto> getMyPostList(Map<String, Object> paramMap);
	
	// 전체 기능 정리
	public QnaDto getQna(int qnaNo);
	
	// 게시글 삭제
	public int deleteQna(int qnaNo);
	
	// 답변 추가
	public int insertReply(QnaDto qna);
	
	// 전체 게시글 조회
	public List<QnaDto> getAllQnalist();
	
	// 전체 갯수 숫자
	public int getQnaCount(int checkFlag);
	
	// 특정 게시글 조회
	public int qnaTotalCount();	
	
	// 게시글 첨부파일 연동
	public List<QattachDto> getQattachList(int qnaNo);
	
	// 첨부파일 테이블
	public QattachDto getQattach(int qattachNo);
    
    // 특정 게시글 댓글 리스트 
    public List<QnaDto> getCommentList(Map<String, Object> map);
}