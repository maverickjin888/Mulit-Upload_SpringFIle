package bitplace.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import bitplace.dao.Bit_Rep;
import bitplace.vo.Bean;
import bitplace.vo.Form_data;

import com.jcabi.github.Commits;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Issue;
import com.jcabi.github.Pull;
import com.jcabi.github.Pulls;
import com.jcabi.github.Repo;
import com.jcabi.github.RepoCommit;
import com.jcabi.github.RepoCommits;
import com.jcabi.github.RtGithub;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.immutable.ArrayMap;

@Controller
@RequestMapping("ajax/groups/{service}")
public class Group_Controller{	

	@Autowired
	Bit_Rep repository;

	Github github;
	Repo repo;
	
	static Integer pathNum = 0;

	boolean logged = false;
	

	
	@ModelAttribute("ajax_data")
	public Object data(HttpServletRequest request, 
			          HttpSession session,
					  @PathVariable("service")String service,
					  Bean bean,
			          Form_data fd
					  ){
		Object res = null;

		//sends group number to the body 			
		res = request.getParameter("groupNo");		

		switch(service){		
		
		case "logged_mainPage":		
			res = repository.getLogged(bean.getMemno());
			break;
		
		case "insertGroup" :
			System.out.println("InsertGroup_Activated_result :"+bean.getGroupno()+" "+bean.getMemno());
			bean.setCreator(bean.getMemno());
			System.out.println("InsertGroup_Activated_result :"+bean.getGroupno()+" "+bean.getMemno());
			System.out.println("InsertGroup_Activated_result :"+bean.getActivegroup());
			
			repository.getInsertGroup(bean);
			System.out.println("InsertGroup_Activated_result :"+bean.getGroupno()+" "+bean.getMemno());
			System.out.println("InsertGroup_Activated_result :"+bean.getActivegroup());
			
			repository.getRegGroupMember(bean);
			res = bean;
			break;
			
		case "searchGroup" : 
			
			bean.setMemno((Integer)session.getAttribute("memNo"));
			String searchValue = "%";
			searchValue+=request.getParameter("data");
			searchValue+="%";
			res = repository.getSearchGroup(bean);			
			break;

		case "searchGroupDetail":
			res = repository.getSearchGroupDetail(Integer.parseInt(request.getParameter("data")));
		break;
		
		case "displayContent" :			
			
			bean.setGit_repository(request.getParameter("git_rep"));
			bean.setGit_id(request.getParameter("git_id"));
			bean.setGit_pwd(request.getParameter("git_pwd"));
			bean.setContentno(Integer.parseInt(request.getParameter("contentno")));
			session.setAttribute("session_bean", bean);
			bean.setSha(request.getParameter("commitsha"));
			
			github = new RtGithub(bean.getGit_id(),bean.getGit_pwd());				

			repo = github.repos().get(
					new Coordinates.Simple(bean.getGit_id(),bean.getGit_repository())
					);	
			
			/*
			 * finding the repository 
			 */
			
			ArrayList<String> shas_trees = new ArrayList<String>();
			String sha_of_tree = null;
			JsonResponse resp = null;
			ArrayList<Bean> list = new ArrayList<Bean>();
			Bean beanz;
			
			try {
				resp = new RtGithub().entry()
						.uri().path("/repos/" +bean.getGit_id()+"/" + bean.getGit_repository()+"/commits")
										.back()
										.fetch()
										.as(JsonResponse.class);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			
			if(!request.getParameter("commitsha").equals("undefined")){
				System.out.println("gogogo : "+request.getParameter("commitsha"));
				sha_of_tree = request.getParameter("commitsha");
			}else{
				sha_of_tree = resp.json().readArray().getJsonObject(0).getJsonObject("commit").getJsonObject("tree").getString("sha");
			}
			
			list = filePathReader(new ArrayList<Bean>(), sha_of_tree,"", 0, 0);
			
			//receive level in current Group
			
			bean.setMemno((Integer) session.getAttribute("memNo"));
			
			String Level =(String) repository.getMemLevel(bean);			
			bean.setLevel(Level.charAt(0));
			
			//receive data from github
			bean.setObject1(list);
			
			//receive data from mysql
			bean.setObject2(repository.getDisplayContent(bean.getContentno()));
			bean.setObject3(repository.getComments(bean.getContentno()));
			res = bean;
						
			break;

			
		case "content_commitinfo_all":
			//			RepoCommits commits = repo.commits();
			//			Iterator<RepoCommit> iterator = commits.iterate(new ArrayMap<String, String>()
			//					/*.with("since","2014-01-26T00:00:00Z")
			//						.with("until","2014-10-27T00:00:00Z")*/).iterator();
			//			ArrayList<String> shas_commits = new ArrayList<String>();
			//
			//			while(iterator.hasNext()){
			//				shas_commits.add(iterator.next().sha());
			//			}
			//			JsonObject tree_of_commit=null;
			//			String sha_of_tree = null;
			//
			//			ArrayList<String> shas_trees = new ArrayList<String>();
			//			Commits commits1 = repo.git().commits();
			//			for(String commit_string : shas_commits){
			//				try {
			//					tree_of_commit = commits1.get(commit_string).json().getJsonObject("tree");
			//					sha_of_tree  = tree_of_commit.getString("sha");
			//					shas_trees.add(sha_of_tree);
			//				} catch (IOException e) {
			//					// TODO Auto-generated catch block
			//					e.printStackTrace();
			//				}
			//			}	
			//			res = shas_trees;
			//			break;

		case "content_commitinfo":
			/*
			bean = (Bean) session.getAttribute("session_bean");			
			ArrayList<String> shas_trees = new ArrayList<String>();
			String sha_of_tree = null;
			JsonResponse resp = null;
			
			try {
				resp = new RtGithub().entry()
						.uri().path("/repos/" +bean.getGit_id()+"/" + bean.getGit_repository()+"/commits")
										.back()
										.fetch()
										.as(JsonResponse.class);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			sha_of_tree = resp.json().readArray().getJsonObject(0).getJsonObject("commit").getJsonObject("tree").getString("sha");
			shas_trees.add(sha_of_tree);
			res = shas_trees;
			*/
			break;


		case "content_commitinfo_search1" :	
			/*
			ArrayList<Bean> list = new ArrayList<Bean>();
			Bean bean2;
			try {
				JsonArray value = repo.git().trees().get(request.getParameter("sha")).json().getJsonArray("tree");
				JsonObject value_jsonObject = null;

				for(int i=0; i<value.size(); i++){
					bean2 = new Bean();
					value_jsonObject = value.getJsonObject(i);
					bean2.setPath(value_jsonObject.getString("path"));
					bean2.setSha(value_jsonObject.getString("sha"));
					bean2.setType(value_jsonObject.getString("type"));
					list.add(bean2);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			res = list;
			*/
			break;

		case "content_commitinfo_contents" :
			String decoded_Bytes_inString = null;				
			try {
				byte[] encoded_Bytes = repo.git()
						 	           .blobs().get(bean.getSha())
						 	           .json().getString("content").getBytes();
				byte[] decoded_Bytes = Base64.decodeBase64(encoded_Bytes);
				decoded_Bytes_inString = new String(decoded_Bytes);
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			res = decoded_Bytes_inString;				
			break;
			
		case "content_update" :
		    
			/*
			 * under construction 
			 */
			System.out.println("updateContent______");


			Github github = new RtGithub(bean.getGit_id(),bean.getGit_pwd());
			Repo repo = github.repos().get(
	                                       new Coordinates.Simple(bean.getGit_id(),bean.getGit_repository())
	                                       );
			
	
			JsonObjectBuilder builder2 = Json.createObjectBuilder();	
			byte[] encodedBytes;
			
			if(fd.getList()!=null){
			for(Bean beanUpdate : fd.getList()){
				System.out.println("___________content_update____________");
				System.out.println(beanUpdate.getPath());
				System.out.println(beanUpdate.getSha());
				System.out.println(beanUpdate.getUpdate_data());
				
					encodedBytes = Base64.encodeBase64(beanUpdate.getUpdate_data().getBytes());
					
				try {
					repo.contents().update(beanUpdate.getPath(),
							builder2
							.add("message", "update from bithub yoho")
							.add("sha", beanUpdate.getSha())
							.add("content", new String(encodedBytes)).build());
					
	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			}
			
			if(fd.getList2()!=null){
				for(Bean beanUpdate : fd.getList2()){
					System.out.println("fd.getList2()_______"+beanUpdate.getSha());
					System.out.println("fd.getList2()_______"+beanUpdate.getPath());
					
					try {
						repo.contents().remove(builder2.add("message", "deleted from github")
								.add("path", beanUpdate.getPath())
								.add("sha", beanUpdate.getSha()).build());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	
				}
			}
			
			if(fd.getList3()!=null){
				for(Bean beanUpdate : fd.getList3()){
										
					decoded_Bytes_inString = null;		
					
					System.out.println("beanUpdate Content_______ : "+beanUpdate.getNewcontent());
					System.out.println("beanUpdate Path__________ : "+beanUpdate.getPath());

					
					if(!beanUpdate.getNewcontent().equals("false") || beanUpdate.getSha().equals("undefined")){		
						
						System.out.println("beanUpdate______finalBlow : ");
						
						encodedBytes = Base64.encodeBase64(beanUpdate.getNewcontent().getBytes());
						try {
							repo.contents().create(Json.createObjectBuilder()
									.add("path",beanUpdate.getPath())
									.add("message","created file from bithub")
									.add("content",new String(encodedBytes))
									.build());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
					}else{
						
					if(!beanUpdate.getSha().equals("undefined") || beanUpdate.getNewcontent().equals("false")){	
						System.out.println("beanUpdate______finalBlowBreak");
					try {
						encodedBytes = repo.git()
								 	           .blobs().get(beanUpdate.getSha())
								 	           .json().getString("content").getBytes();
						byte[] decoded_Bytes = Base64.decodeBase64(encodedBytes);
						decoded_Bytes_inString = new String(decoded_Bytes);
						
						encodedBytes = Base64.encodeBase64(decoded_Bytes_inString.getBytes());
																		
						repo.contents().create(Json.createObjectBuilder()
								.add("path",beanUpdate.getPath())
								.add("message","moved by bithub")
								.add("content",new String(encodedBytes)) //this is a encoded string using base64
								.build());						
						
						repo.contents().remove(builder2.add("message", "deleted from bithub")
								.add("path", beanUpdate.getPathBefore())
								.add("sha", beanUpdate.getSha()).build());
						
						
												
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					}
				}
				}
			}


			String sha_of_commit = null;
		    resp = null;
		    
		    /*
		     * insert into Mysql
		     */
			
			try {
				resp = new RtGithub().entry()
						.uri().path("/repos/" +bean.getGit_id()+"/" + bean.getGit_repository()+"/commits")
										.back()
										.fetch()
										.as(JsonResponse.class);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			sha_of_commit = resp.json().readArray().getJsonObject(0).getJsonObject("commit").getJsonObject("tree").getString("sha");			
			bean.setCommit(sha_of_commit);
			repository.updateContent(bean);			
			break;
		
		case "download_Content" : 
			
			Integer userid = (Integer)session.getAttribute("memNo");
			String folderPath = "/Users/hongdavid/git/Bit_Place/src/main/webapp/resources/"+userid+"_download/";
			
			Github githubContent = new RtGithub(bean.getGit_id(),bean.getGit_pwd());
			repo = githubContent.repos().get(
	                 new Coordinates.Simple(bean.getGit_id(),bean.getGit_repository())
	        );
			
			String filename = bean.getPath().substring(bean.getPath().lastIndexOf('/')+1);
			String zipfile = filename.substring(0,filename.lastIndexOf('.'));
			byte[] buffer = new byte[1024];

			try {
				byte[] encoded_Bytes = repo.git()
						 	           .blobs().get(bean.getSha())
						 	           .json().getString("content").getBytes();
							
				byte[] decoded_Bytes = Base64.decodeBase64(encoded_Bytes);
				//decoded_Bytes_inString = new String(decoded_Bytes);
				
				File createDir = new File(folderPath);
				File createFile = new File(folderPath+filename);
					
				if(!createDir.exists()){
					createDir.mkdir();
				}
								
				FileOutputStream fos = new FileOutputStream(createFile);
				fos.write(decoded_Bytes);

				
				/*
				 * Compress the file 
				 */
				
				//name the zipfile 
				System.out.println("ZipTest_____________"+zipfile);
				
				fos = new FileOutputStream(folderPath+zipfile+".zip");
				
				System.out.println("ZipTest_____________"+folderPath+zipfile+".zip");

			    //zipfile
				ZipOutputStream zos = new ZipOutputStream(fos);
				ZipEntry ze = new ZipEntry(filename);
				zos.putNextEntry(ze);
				System.out.println("ZipTest_____________"+folderPath+filename);
				FileInputStream in = new FileInputStream(folderPath+filename);

				int len;
				
				while ((len = in.read(buffer)) > 0) {
	    			zos.write(buffer, 0, len);
	    		}
	 
	    		in.close();
	    		zos.closeEntry();
	 
	    		//remember close it
	    		zos.close();
				
				/*
				 * Compress the file end 
				 */
				
				
				fos.flush();
				fos.close();
								
			
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String resultPath = folderPath+zipfile+".zip";
			System.out.println("______result : "+resultPath.substring(resultPath.lastIndexOf("/resources")));
		
			res = "/Bit_Place"+resultPath.substring(resultPath.lastIndexOf("/resources"));		
		break;

		case "displayContentByTitle" : 		
			logged = Boolean.parseBoolean(request.getParameter("logged"));
			
				if(logged != true)
				bean.setGroupno(Integer.parseInt(request.getParameter("groupno")));
				else
				bean.setGroupno((Integer)session.getAttribute("FirstGroup"));
		
				System.out.println("DisplayContentByTitle________"+bean.getGroupno()+bean.getTitleno());
				res = repository.getContentfromOneTitle(bean);
			break;

		case "submitPost":			
			Integer memno = (Integer) session.getAttribute("memNo");
			Integer groupno= Integer.parseInt(request.getParameter("groupNo"));			
			
			bean = (Bean) session.getAttribute("session_bean");
			bean.setMemno(memno);
			bean.setGroupno(groupno);
			res = repository.submitPost(bean);	
			break;
			
		case "personalInfo":
			res="this is the personalInfo";
			break;		

		
		case "forkRepo" :
			String REMOTE_URL = "https://github.com/"+bean.getGit_id()+"/"+bean.getGit_repository()+".git";
			String LOCAL_PATH = "/Users/hongdavid/git/Bit_Place/src/main/webapp/resources/";
			File localPath = new File(LOCAL_PATH+"/"+bean.getGit_id()+".git");

			System.out.println("Cloning from " + REMOTE_URL + " to " + localPath);
		
			try {
				Git.cloneRepository()
				.setURI(REMOTE_URL)
				.setDirectory(localPath)
				.call();
			} catch (InvalidRemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (TransportException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (GitAPIException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// now open the created repository that is in local disk
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
			Repository repository = null;
			try {
				repository = builder.setGitDir(localPath)
						.readEnvironment() // scan environment GIT_* variables
						.findGitDir() // scan up the file system tree
						.build();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//end of cloning
		break;
			
		}
		
	
		return res;
	}

	ArrayList<Bean> filePathReader(ArrayList<Bean> list, String sha_of_tree, String path, Integer pathNum, Integer pathNumParent){
		
		try {
			JsonArray value = repo.git().trees().get(sha_of_tree).json().getJsonArray("tree");
			JsonObject value_jsonObject = null;
			Bean beanz;
			
			for(int i=0; i<value.size(); i++){
				beanz = new Bean();
				value_jsonObject = value.getJsonObject(i);
				
		
				pathNum=list.size();	
                System.out.println("size_of_the_list : "+list.size());
				
				beanz.setPathNum(pathNum);
				beanz.setPathNumParent(pathNumParent);
				
				
				beanz.setPath(path+"/"+value_jsonObject.getString("path"));
				beanz.setSha(value_jsonObject.getString("sha"));
				beanz.setType(value_jsonObject.getString("type"));				
				
				if(beanz.getPathNumParent()==0){
					path="";
				}				
				
				if(beanz.getType().equals("tree")){
					path+="/"+value_jsonObject.getString("path");
					beanz.setPath(path);
					
					System.out.println("dir_Output_ : "+beanz.getPath()+" "+beanz.getPathNum()+" "+beanz.getPathNumParent());

					/*
					 * test
					 */
					beanz.setPathNum(pathNum+100);
					/*
					 * test end 
					 */
					list.add(beanz);
					filePathReader(list, beanz.getSha(), beanz.getPath(), pathNum, beanz.getPathNum());
				}else if(beanz.getType().equals("blob")){
					System.out.println("data_Output_ : "+beanz.getPath()+" "+beanz.getPathNum()+" "+beanz.getPathNumParent());
					beanz.setPathNum(pathNum+=1);
					list.add(beanz);
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	
	@RequestMapping
	public String view(@PathVariable("service") String service, HttpServletRequest request){		

		if(service.equals("content_commitinfo_search1")){
			Integer sides = Integer.parseInt(request.getParameter("sides"));
			request.setAttribute("sides", sides);	
		}	
		
		if(service.equals("content_update")){
			return "/main/groups/redirect_Group";
		}else if(service.equals("displayContentByTitle")){
			return "/main/frame/subSearch";
		}else{		
		    return "/main/groups/"+service;
		}
	}	
}
