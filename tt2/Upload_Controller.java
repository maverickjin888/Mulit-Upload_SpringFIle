package bitplace.controller;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.json.Json;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.gson.Gson;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Repo;
import com.jcabi.github.RtGithub;

import bitplace.dao.Bit_Rep;
import bitplace.vo.Bean;
import bitplace.vo.Message;
import bitplace.vo.StatusResponse;
import bitplace.vo.UploadedFile;

@Controller
@RequestMapping("/upload")
public class Upload_Controller {
	
	// 개발자 장윤용
	@Autowired
	ServletContext servletContext;
	// 개발자 장윤용 끝 
	
	@Autowired
	Bit_Rep Repository;
	
	@RequestMapping
	public String form() {
		return "form";
	}
	
	String userid = "maverickjin8";
	ArrayList<File> fileArray = new ArrayList<File>();
	String folderPath = "/Users/hongdavid/git/Bit_Place/src/main/webapp/resources/"+userid+"/";
	
	@RequestMapping(value="/message", method=RequestMethod.POST)
	public @ResponseBody StatusResponse message(@RequestBody Message message) {	
		
		String folder = message.getFolder();		
		String files = message.getFilename();
		String[] fileNames = files.split(",");
	    
		File createFolder = new File(folderPath+folder);
		System.out.println(createFolder.mkdirs());
		
	    for(int i=0; i<fileNames.length; i++){
	     	  	     	 
	     File afile =new File(folderPath+fileNames[i]);
	    	 if(afile.renameTo(new File(folderPath+folder+"/"+fileNames[i]))){	    		 
	    		System.out.println("File moved successful!");
	    	   }else{
	    		System.out.println("File failed to move!");
	    	 }	    	
	    }				
		return new StatusResponse(true, "Message received");
	}
	
	@RequestMapping(value="/upload")
	public String upload(HttpServletRequest request, HttpSession session){
		Bean beanPost = new Bean();		
		beanPost.setMemno(Integer.parseInt(request.getParameter("memno")));
		beanPost.setTitleno(Integer.parseInt(request.getParameter("titleno")));
		beanPost.setContent_title(request.getParameter("content_title"));
		beanPost.setOpento(request.getParameter("opento").charAt(0));
		beanPost.setContent(request.getParameter("content"));
		beanPost.setGit_id(request.getParameter("git_id"));
		beanPost.setGit_repository(request.getParameter("git_repo"));
		beanPost.setGit_pwd(request.getParameter("git_pwd"));
		String folderPath = "/Users/hongdavid/git/Bit_Place/src/main/webapp/resources/"+userid;

		Github github = new RtGithub(beanPost.getGit_id(),beanPost.getGit_pwd());				
		if(request.getParameter("git_repo").substring(0,8).equals("[github]") ){
			
			/*
			 * receiving data from github  
			 */
			
			String REMOTE_URL = "https://github.com/"+beanPost.getGit_id()+"/"+request.getParameter("git_repo").substring(8)+".git";
			String LOCAL_PATH = "/Users/hongdavid/git/Bit_Place/src/main/webapp/resources/";
			File localPath = new File(LOCAL_PATH+"/"+beanPost.getGit_id()+".git");

			// then clone
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

			/*
			 * receiving data from github end
			 */
			
			beanPost.setGit_repository(request.getParameter("git_repo").substring(9)+(int)(Math.random()*99999));
			folderPath = "/Users/hongdavid/git/Bit_Place/src/main/webapp/resources/"+userid+".git";
		}				
		
		Repo repo;
			try {
				 repo = github.repos().create(Json.createObjectBuilder()
			    .add("name", beanPost.getGit_repository())
	            .add("auto_init",true)
	            .build());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

		ArrayList<File> list = findFile(new File(folderPath), new ArrayList<File>());
				
		
		repo = github.repos().get(
				   	new Coordinates.Simple(beanPost.getGit_id(),beanPost.getGit_repository())
				    );			
		
		FileInputStream fstream = null;
		DataInputStream in = null;
		BufferedReader br = null;
		File f;
		
    	for(int s=0; s<list.size(); s++){
				String values="";					
				  try{
					    fstream = new FileInputStream(list.get(s));
					    in = new DataInputStream(fstream);
					    br = new BufferedReader(new InputStreamReader(in));
					    String strLine;
					    while ((strLine = br.readLine()) != null)   {
					    	values+=strLine+"\n";				    				    		
					    }

					    //path for github
						Integer bufnum = list.get(s).getAbsolutePath().lastIndexOf(userid);
						
						/*
						 * under construction
						 */
		
						String path;
						
						if(request.getParameter("git_repo").substring(0,8).equals("[github]") ){			
						     path = "clone_"+list.get(s).getAbsolutePath().substring(bufnum+userid.length()+5); 
						}else{
							System.out.println("+1isworking");
						     path = list.get(s).getAbsolutePath().substring(bufnum+userid.length()+1);
						}
	
					    System.out.println("Path_for_github --> "+path);

					    //data for github 
					    byte[] encodedBytes = Base64.encodeBase64(values.getBytes());
						
					
						repo.contents().create(Json.createObjectBuilder()
								.add("path",path)
								.add("message","made from Bit_Place")
								.add("content",new String(encodedBytes)) 
								.build());
						
				}catch (Exception e){}
				finally{
					try {br.close();} catch (IOException e){}
					try {in.close();} catch (IOException e){}
					try {fstream.close();} catch (IOException e){}
				}
			}						
	    	
    	f = new File("/Users/hongdavid/git/Bit_Place/src/main/webapp/resources/"+userid);
    	try {
			FileUtils.cleanDirectory(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	f = new File("/Users/hongdavid/git/Bit_Place/src/main/webapp/resources/"+userid+".git");
    	try {
			FileUtils.cleanDirectory(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	
		Repository.inputSubmitPost(beanPost); 
		Repository.inputSubmitPost2(beanPost);
		return "/main/groups/home";
	}

	
	public ArrayList<File> findFile(File file, ArrayList<File>results){
		
        File[] list = file.listFiles();
        
        for(File fil : list){
        	if(fil.isDirectory()){
        		
        		if(!fil.isHidden()){
        		File filez = new File(fil.getAbsolutePath());
        		System.out.println("find_directory : "+fil.getAbsolutePath());
        		findFile(filez, results);
        		}
        	}
        	else{
        		if(!fil.isHidden()){
        		int buffer1 = fil.getAbsolutePath().lastIndexOf(userid);
        		int buffer0 = userid.length();
        		System.out.println("find_files : "+fil.getAbsolutePath());
        		File f = new File(fil.getAbsolutePath());
        		String buffer2 = fil.getAbsolutePath().substring(buffer1+buffer0+1);        	        		
        		String [] buffer3 = buffer2.split("/");  		
        		results.add(f);	
        		}
        	}        	
        }        
    	return results;
	}
	
	List<File> textFiles(String directory) {
	
		  List<File> textFiles = new ArrayList<File>();
		  File dir = new File(directory);
		  for (File file : dir.listFiles()) {
		      textFiles.add(file);
		  }
		  return textFiles;
	}
	
	@RequestMapping(value="/file", method=RequestMethod.POST)
	public @ResponseBody List<UploadedFile> upload(
		   @RequestParam("file") MultipartFile file) {
		
		List<UploadedFile> uploadedFiles = new ArrayList<UploadedFile>();
		UploadedFile u = new UploadedFile(file.getOriginalFilename(),
				Long.valueOf(file.getSize()).intValue(),
				"/Users/hongdavid/git/Bit_Place/src/main/webapp/resources/"+file.getOriginalFilename());

		String path = "/Users/hongdavid/git/Bit_Place/src/main/webapp/resources/"+userid+"/";
		String filename = file.getOriginalFilename();
		File files = new File(path+filename);

		System.out.println("added to arrayList");
		fileArray.add(files);
				
		try{
			byte[] byteArr = file.getBytes();
			FileOutputStream fos = new FileOutputStream(files);
			fos.write(byteArr);
			fos.close();
		}catch(Exception e){}

		uploadedFiles.add(u);		
		return uploadedFiles;
	}
	
	//개발자 장윤용
		@RequestMapping(value = "/photoChange", method = RequestMethod.POST)
		  @ResponseBody
		  public String photoChange( Model model, MultipartHttpServletRequest request, Bean bean) throws Exception {
			
			Iterator<String> itr =  request.getFileNames();
		      if(itr.hasNext()) {
		          MultipartFile mpf = request.getFile(itr.next());
		          System.out.println(mpf.getOriginalFilename() +" uploaded!");
		          String uploadDir = servletContext.getRealPath("/img/userimages");
		          File uploadFile = new File(uploadDir + "/" + mpf.getOriginalFilename());
		         
		          mpf.transferTo(uploadFile);
		          
		          //output 
		          String photo = mpf.getOriginalFilename();
		          model.addAttribute("photo", photo);
		          System.out.println("파일 존재" + mpf.isEmpty());		         
		         return new Gson().toJson(photo);
		          
		      } else {
		      	return "fail";
		      }
		  }
	//개발자 장윤용 끝 
	
}
