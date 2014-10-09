package bitplace.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.json.Json;
import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eclipse.egit.github.core.service.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jcabi.github.Github;
import com.jcabi.github.Repo;
import com.jcabi.github.RtGithub;
import com.jcabi.log.Logger;

import bitplace.dao.Bit_Rep;
import bitplace.vo.Bean;
import bitplace.vo.EmailTest;
import bitplace.vo.Form_data;
import bitplace.vo.Coolsms.Coolsms;
import bitplace.vo.Coolsms.SendResult;
import bitplace.vo.Coolsms.Set;

import org.apache.commons.lang3.RandomStringUtils;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;


@Controller
@RequestMapping("ajax/personal/{service}")
public class Personal_Controller {

@Autowired
Bit_Rep repository;

Github github;


	String[] redirects ={"groupAdminTitle_submit"
		             ,"memberChange_btn_submit"
		             ,"personal_answerInvite"
		             ,"groupChange_btn_submit"
		             ,"groupAdminTitle_delete"
		             ,"groupAdmin_deleteIndividuals"
		             ,"groupAdmin_deleteIndividuals"
	  	              };

	@ModelAttribute("data")
	public Object data(@PathVariable("service") String service,
			           HttpServletRequest request,
			           HttpSession session,
			           Bean bean,
			           Form_data fd
					   ){			
		Object res = null;		
		switch(service){
		
		/*
		 * 개발자 장윤용 
		 */
		
				case "messageAlarm":
					Integer memNo10 = (Integer)session.getAttribute("memNo");
					List<Bean> isAlarmed = (List<Bean>)repository.getIsAlarmed(memNo10);
					ArrayList<Bean> messageAlarms = new ArrayList<Bean>();
					if(isAlarmed.size()>0){
						System.out.println(isAlarmed.size()>0);
					
					for(int i=0; i<isAlarmed.size(); i++){
						if(isAlarmed.get(i).getGroupno()==0){
							System.out.println("getGroupno()==0");
							bean = (Bean)repository.getMember(memNo10);
							if(bean.isAlarm() == true){
								 bean = (Bean)repository.getMessageForAlarm(isAlarmed.get(i));
								 if(!bean.getPhoto().equals("https://avatars.githubusercontent.com/u/7775019?v=2")){
										String[] photoArr = {"../img/userimages/", bean.getPhoto()};
										String userPhoto = concatenate(photoArr);
										bean.setPhoto(userPhoto);
										}
								 bean.setRoomno(isAlarmed.get(i).getRoomno());
								 bean.setGroupno(isAlarmed.get(i).getGroupno());
								 messageAlarms.add(bean);
							}
						}else{
							System.out.println("getGroupno()!=0");
							isAlarmed.get(i).setMemno(memNo10);
							bean = (Bean)repository.getGroupAlarm(isAlarmed.get(i));
							if(bean.isAlarm()==true){
								 bean = (Bean)repository.getMessageForAlarm(isAlarmed.get(i));
								 if(!bean.getPhoto().equals("https://avatars.githubusercontent.com/u/7775019?v=2")){
										String[] photoArr = {"../img/userimages/", bean.getPhoto()};
										String userPhoto = concatenate(photoArr);
										bean.setPhoto(userPhoto);
										}
								 bean.setRoomno(isAlarmed.get(i).getRoomno());
								 bean.setGroupno(isAlarmed.get(i).getGroupno());
								 messageAlarms.add(bean);
							}
						}
					}
					}/*else{
						System.out.println("isAlarm.size() == 0");
						bean.setMemno(0);
						messageAlarms.add(bean);
						
					}*/
					
					List<Bean> invitationAlarms = (List<Bean>)repository.getInvitationForAlarm(memNo10);
					if(invitationAlarms != null){
						System.out.println("invitationAlarms != null");
						for(int i=0; i<invitationAlarms.size(); i++){
							if(!invitationAlarms.get(i).getPhoto().equals("https://avatars.githubusercontent.com/u/7775019?v=2")){
								String[] photoArr = {"../img/userimages/", invitationAlarms.get(i).getPhoto()};
								String inviterPhoto = concatenate(photoArr);
								invitationAlarms.get(i).setPhoto(inviterPhoto);
								}
						}
					}
					
					List<Bean> requestAlarms = (List<Bean>)repository.getJoinRequestForAlarm(memNo10);
					if(requestAlarms != null){
						System.out.println("requestAlarms != null");
						for(int i=0; i<requestAlarms.size(); i++){
							if(!requestAlarms.get(i).getPhoto().equals("https://avatars.githubusercontent.com/u/7775019?v=2")){
								String[] photoArr = {"../img/userimages/", requestAlarms.get(i).getPhoto()};
								String rquesterPhoto = concatenate(photoArr);
								requestAlarms.get(i).setPhoto(rquesterPhoto);
								}
						}
					}
					
					repository.updateIsAlarmed(memNo10);
					repository.invitationIsAlaremd(memNo10);
					repository.joinRequestIsAlaremd(memNo10);
					
					bean.setObject1(messageAlarms);
					bean.setObject2(invitationAlarms);
					bean.setObject3(requestAlarms);
					
					res=bean;
					
				break;
				
				case "messageContainerFromAlarm" :
					Integer memNo11=(Integer)(session.getAttribute("memNo"));
					List<Bean> roomnos3 = (List<Bean>)repository.getRoomno(memNo11);
					ArrayList<Bean> messageTitle2 = new ArrayList<Bean>();
					Integer totalCount4 = 0;
					for(Bean roomno : roomnos3){
						roomno.setMemno(memNo11);
						bean = (Bean)repository.getReadCount(roomno);
						Integer readCount = bean.getCount();
						bean = (Bean)repository.getMessageCount(roomno);
						Integer messageCount = bean.getCount();
						Integer notReadMessage = messageCount - readCount;
						List<Bean> receiverForTitle = (List<Bean>)repository.getReceiverForTitle(roomno);
						bean=(Bean)repository.getLastMessage(roomno);
						bean.setCount(notReadMessage);
						totalCount4 += bean.getCount();
						
							if(receiverForTitle.size()==1){
								String receiverno2=Integer.toString(receiverForTitle.get(0).getMemno());
								receiverForTitle.get(0).setReceiverno(receiverno2);
								if(!receiverForTitle.get(0).getPhoto().equals("https://avatars.githubusercontent.com/u/7775019?v=2")){
									String[] photoArr5 = {"../img/userimages/", receiverForTitle.get(0).getPhoto()};
									String senderPhoto3 = concatenate(photoArr5);
									receiverForTitle.get(0).setPhoto(senderPhoto3);
								}
								bean.setReceiverno(receiverForTitle.get(0).getReceiverno());
								bean.setPhoto(receiverForTitle.get(0).getPhoto());
								bean.setName(receiverForTitle.get(0).getName());
							}
							else if(receiverForTitle.size()==2){
								for(int i=0; i<receiverForTitle.size(); i++){
									if(receiverForTitle.get(i).getMemno().equals(memNo11)){
										continue;
									}
									String receiverno2=Integer.toString(receiverForTitle.get(i).getMemno());
									bean.setReceiverno(receiverno2);
									bean.setName(receiverForTitle.get(i).getName());
									if(!receiverForTitle.get(i).getPhoto().equals("https://avatars.githubusercontent.com/u/7775019?v=2")){
										String[] photoArr5 = {"../img/userimages/", receiverForTitle.get(i).getPhoto()};
										String senderPhoto3 = concatenate(photoArr5);
										bean.setPhoto(senderPhoto3);
									}else{
										bean.setPhoto(receiverForTitle.get(i).getPhoto());
									}
								}
								
							}else{
								String receiversName2 = "";
								String receiversNo2 = "";
								for(int i=0; i<receiverForTitle.size(); i++){
									if(receiverForTitle.get(i).getMemno().equals(memNo11) ){
										continue;
									}
									
									if(i<receiverForTitle.size()-1 ){
										String[] receiverArr2 = {receiverForTitle.get(i).getName(), ", "};
										String receiverName2 = concatenate(receiverArr2);
										receiversName2 += receiverName2;
										
										String receiverno2 = Integer.toString(receiverForTitle.get(i).getMemno());
										String[] receivernoArr2 = {receiverno2, ","};
										String receiverNo2 = concatenate(receivernoArr2);
										receiversNo2 += receiverNo2;
									}
								}
								receiversName2 += receiverForTitle.get(receiverForTitle.size()-1).getName();
								String receiverno2 = Integer.toString(receiverForTitle.get(receiverForTitle.size()-1).getMemno());
								receiversNo2 += receiverno2;
								bean.setReceiverno(receiversNo2);
								bean.setName(receiversName2);
								bean.setPhoto("../img/userimages/default-group-image.gif");
						}
						
							messageTitle2.add(bean);
							
					}
					bean.setObject1(messageTitle2);
					bean.setTotalCount(totalCount4);
					
					res=bean;
				break;
				
				case "personalInfo":
					Integer totalCount3 = 0;
					Integer memno = (Integer)session.getAttribute("memNo");
					List<Bean> roomNos = (List<Bean>)repository.getRoomno(memno);
					for(Bean roomNo : roomNos){
						roomNo.setMemno(memno);
						bean = (Bean)repository.getReadCount(roomNo);
						Integer readCount = bean.getCount();
						bean = (Bean)repository.getMessageCount(roomNo);
						Integer messageCount = bean.getCount();
						Integer notReadMessage = messageCount - readCount;
						totalCount3 += notReadMessage;
					}
					
					bean = (Bean)repository.getMember(memno);
					bean.setObject1(repository.selectGroup(memno));
					bean.setTotalCount(totalCount3);
					
					String photo = bean.getPhoto();
					if(photo.equals("https://avatars.githubusercontent.com/u/7775019?v=2")){
						
						res=bean;
					}else{
						String[] photoArr = {"../img/userimages/", photo};
						String userPhoto = concatenate(photoArr);
						System.out.println(userPhoto);
						bean.setPhoto(userPhoto);
						res=bean;
					}
				break;
				
				case "groupManage" :
					Integer memNo9 = (Integer)session.getAttribute("memNo");
					List<Bean> joinedGroups = (List<Bean>)repository.selectGroup(memNo9);
					List<Bean> requestGroups = (List<Bean>)repository.getGroupFromRequest(memNo9);
					bean.setObject1(joinedGroups);
					bean.setObject2(requestGroups);
					
					res = bean;
				break;
				
				case "followerWithdraw" :
					bean.setGroupno(Integer.parseInt(request.getParameter("groupno")));
					Integer memNo=(Integer)(session.getAttribute("memNo"));
					bean.setMemno(memNo);
					//repository.deleteCommentsByFollower(bean);
					
					
					
					repository.groupWithdraw(bean);
					
					res="success";
					
				break;
				
				case "leaderWithdraw" :
					Integer groupno2=Integer.parseInt(request.getParameter("groupno"));
					repository.deleteInvitation(groupno2);
					repository.deleteComments(groupno2);
					
					List<Bean> contentnos = (List<Bean>)repository.getContentno(groupno2);
					if(contentnos != null){
					for(Bean contentno : contentnos){
						repository.deleteContentSpecific(contentno.getContentno());
						repository.deleteReadOrNot(contentno.getContentno());
					}
					}
					
					List<Bean> titlenos = (List<Bean>)repository.getTitleno(groupno2);
					if(titlenos != null){
					for(Bean titleno : titlenos){
						repository.deleteContent(titleno.getTitleno());
						repository.deleteTitleOrder(titleno.getTitleno());
					}
					}
					
					repository.deleteTitle(groupno2);
					
					List<Bean> leadnos = (List<Bean>)repository.getLeadno(groupno2);
					if(leadnos !=null){
					for(Bean leadno : leadnos){
						repository.deleteAnnounce(leadno.getLeadno());
						repository.deleteData(leadno.getLeadno());
					}
					}
					
					repository.deleteLeaderWrite(groupno2);
					
					repository.groupMembersWithdraw(groupno2);
					repository.groupDelete(groupno2);
					
					res="success";
					
				break;
				
				case "alarmChecked" :
					Integer memNo2=(Integer)(session.getAttribute("memNo"));
					bean.setMemno(memNo2);
					String alarms = request.getParameter("alarms");
					System.out.println(alarms);
					String[] alarmsArr = alarms.split(",");
					for(int i=0; i<alarmsArr.length; i++){
						String[] groupnoAlarm = alarmsArr[i].split(":");
						Integer groupno = Integer.parseInt(groupnoAlarm[0]);
						Boolean alarm = Boolean.parseBoolean(groupnoAlarm[1]);
						bean.setGroupno(groupno);
						bean.setAlarm(alarm);
						
						repository.alarmChecked(bean);
						System.out.println(groupno);
						System.out.println(alarm);
					}
					
					res="success";
					
				break;
				
				case "messageWrite" :
					res="message";
				
				break;
				
				case "receiverSearch" :
					System.out.println(request.getParameter("search"));
					bean.setSearch(request.getParameter("search"));
					List<Bean> searchResults = repository.receiverSearch(bean);
					ArrayList<Bean> viewResults = new ArrayList<Bean>();
					for(Bean result : searchResults){
						if(result.getPhoto().equals("https://avatars.githubusercontent.com/u/7775019?v=2")){
							viewResults.add(result);
						}else{
							String[] photoArr2 = {"../img/userimages/", result.getPhoto()};
							String resultPhoto = concatenate(photoArr2);
							result.setPhoto(resultPhoto);
							viewResults.add(result);
						}
					}
					res=viewResults;
					
				break;
				
				case "getGroupname" :
					Integer memNo3=(Integer)(session.getAttribute("memNo"));
					res=repository.getGroupname(memNo3);
					
				break;
				
				case "getGroupMembersName" :
					Integer memNo4=(Integer)(session.getAttribute("memNo"));
					Bean senderName = (Bean)repository.getMember(memNo4);
					bean.setGroupno(Integer.parseInt(request.getParameter("groupno")));
					List<Bean> getNames = (List<Bean>)repository.getGroupMembersName(bean);
					for(int i=0; i<getNames.size(); i++){
						if(getNames.get(i).getName().equals(senderName.getName())){
							getNames.get(i).setName("");
							continue;
						}
						if(i!=getNames.size()-1 && !getNames.get(getNames.size()-1).getName().equals(senderName.getName())){
							String[] nameArr = {getNames.get(i).getName(), ", "};
							String memberName = concatenate(nameArr);
							getNames.get(i).setName(memberName);
						} 
					}
					res=getNames;	
					
				break;
				
				case "messageSend" :
					Integer roomnoCount = 1;
					Integer memNo5=(Integer)(session.getAttribute("memNo"));
					String receiver = request.getParameter("receiver");
					String[] participants = receiver.split(",");
					bean = repository.getMaxRoomno();
					
					if(bean==null){
						Bean bean2 = new Bean();
						bean2.setM_content(request.getParameter("messageContent"));
						System.out.println("roomno==null");
						bean2.setRoomno(roomnoCount);
						bean2.setGroupno(Integer.parseInt(request.getParameter("groupno")));
						repository.createRoom(bean2);
						bean2.setMemno(memNo5);
						repository.participateRoom(bean2);
						repository.sendMessage(bean2);
						bean2 = repository.getMessageno(bean2);
						bean2.setMemno(memNo5);
						bean2.setRoomno(roomnoCount);
						repository.readMessage(bean2);
						if(Integer.parseInt(participants[0]) != memNo5){
							for(int i=0; i<participants.length; i++){
								bean2.setMemno(Integer.parseInt(participants[i]));
								bean2.setAlarm(false);
								repository.participateRoom(bean2);
								repository.messageIsAlarmed(bean2);
							}
						}
						List<Bean> messageInfo = (List<Bean>)repository.getMessage(roomnoCount);
						bean2.setMemno(memNo5);
						for(int i=0; i<messageInfo.size(); i++){
							if(!messageInfo.get(i).getPhoto().equals("https://avatars.githubusercontent.com/u/7775019?v=2")){
								String[] photoArr3 = {"../img/userimages/", messageInfo.get(i).getPhoto()};
								String senderPhoto = concatenate(photoArr3);
								messageInfo.get(i).setPhoto(senderPhoto);
							}
						}
						bean2.setObject1(messageInfo);
						bean = bean2;
					}
					
					else{
						Integer roomno = bean.getRoomno();
						bean.setM_content(request.getParameter("messageContent"));
						++ roomno;
						System.out.println(roomno);
						System.out.println("roomno!=null");
						bean.setRoomno(roomno);
						bean.setGroupno(Integer.parseInt(request.getParameter("groupno")));
						repository.createRoom(bean);
						bean.setMemno(memNo5);
						repository.participateRoom(bean);
						repository.sendMessage(bean);
						bean = repository.getMessageno(bean);
						bean.setMemno(memNo5);
						bean.setRoomno(roomno);
						repository.readMessage(bean);
							for(int i=0; i<participants.length; i++){
								if(Integer.parseInt(participants[i]) != memNo5){
								bean.setMemno(Integer.parseInt(participants[i]));
								bean.setAlarm(false);
								repository.participateRoom(bean);
								repository.messageIsAlarmed(bean);
							}
						}
						List<Bean> messageInfo = (List<Bean>)repository.getMessage(roomno);
						bean.setMemno(memNo5);
						for(int i=0; i<messageInfo.size(); i++){
							if(!messageInfo.get(i).getPhoto().equals("https://avatars.githubusercontent.com/u/7775019?v=2")){
								String[] photoArr3 = {"../img/userimages/", messageInfo.get(i).getPhoto()};
								String senderPhoto = concatenate(photoArr3);
								messageInfo.get(i).setPhoto(senderPhoto);
							}
						}
						bean.setObject1(messageInfo);
					}
					
					
					String receiversName = "";
					String receiversNo = "";
					List<Bean> receiverInfo = (List<Bean>)repository.getReceiver(bean);
					if(receiverInfo.size()!=1){
						for(int i=0; i<receiverInfo.size()-1; i++ ){
							if(receiverInfo.get(i).getMemno().equals(memNo5) ){
								System.out.println("senerReceiver="+receiverInfo.get(i).getMemno());
								continue;
							}
							if(i == 0 && receiverInfo.size() == 2){
								receiversName = receiverInfo.get(i).getName();
							}else{
								String[] receiverArr = {receiverInfo.get(i).getName(), ", "};
								String receiverName = concatenate(receiverArr);
								receiversName += receiverName;
							}
							
						}
						if(!receiverInfo.get(receiverInfo.size()-1).getMemno().equals(memNo5)){
							receiversName += receiverInfo.get(receiverInfo.size()-1).getName();
							}
					for(int i=0; i<receiverInfo.size()-1; i++ ){
						String receiverno = Integer.toString(receiverInfo.get(i).getMemno());
						String[] receivernoArr = {receiverno, ","};
						String receiverNo = concatenate(receivernoArr);
						receiversNo += receiverNo;
					}
					String receiverno = Integer.toString(receiverInfo.get(receiverInfo.size()-1).getMemno());
					receiversNo += receiverno;
					
					bean.setName(receiversName);
					bean.setReceiverno(receiversNo);
					}else{
						bean.setName(receiverInfo.get(0).getName());
						bean.setReceiverno(Integer.toString(receiverInfo.get(0).getMemno()));
					}
					res=bean;
					
				break;
				
				case "messageReply" :
					SimpleDateFormat day = new SimpleDateFormat("yyyyMMdd");
					long todaytime = 0;
					String today, lastMessageDay, newMessageDay = null;
					Integer memNo6=(Integer)(session.getAttribute("memNo"));
					Integer roomno3=Integer.parseInt(request.getParameter("roomno"));
					bean.setMemno(memNo6);
					bean.setRoomno(roomno3);
					bean.setM_content(request.getParameter("messageContent"));
					
					todaytime = System.currentTimeMillis();
					today =  day.format(new Date(todaytime));
					Bean lastMessageDate = (Bean)repository.getMaxMessageDate(roomno3);
					lastMessageDay =  day.format(lastMessageDate.getDate());
					
					repository.sendMessage(bean);
					Bean messageno2 = repository.getMessageno(bean);
					bean.setMessageno(messageno2.getMessageno());
					repository.readMessage(bean);
					List<Bean> participants2 = (List<Bean>)repository.getParticipants(bean);
					for(int i=0; i<participants2.size(); i++){
						bean.setMemno(participants2.get(i).getParticipants());
						bean.setAlarm(false);
						repository.messageIsAlarmed(bean);
					}
					
					Bean messageInfo2 = (Bean)repository.getReplyMessage(bean);
					
					if(!messageInfo2.getPhoto().equals("https://avatars.githubusercontent.com/u/7775019?v=2")){
							String[] photoArr4 = {"../img/userimages/", messageInfo2.getPhoto()};
							String senderPhoto2 = concatenate(photoArr4);
							messageInfo2.setPhoto(senderPhoto2);
						}
					
					
					Bean newMessageDate = (Bean)repository.getMaxMessageDate(roomno3);
					newMessageDay = day.format(newMessageDate.getDate());
					
						if(today.equals(newMessageDay) && !today.equals(lastMessageDay)){
							messageInfo2.setToday(true);
						}else{
							messageInfo2.setToday(false);
						}
						
					res=messageInfo2;
			
				break;
				
				case "messageContainer" :
					Integer memNo7=(Integer)(session.getAttribute("memNo"));
					List<Bean> roomnos = (List<Bean>)repository.getRoomno(memNo7);
					ArrayList<Bean> messageTitle = new ArrayList<Bean>();
					Integer totalCount = 0;
					for(Bean roomno : roomnos){
						roomno.setMemno(memNo7);
						bean = (Bean)repository.getReadCount(roomno);
						Integer readCount = bean.getCount();
						bean = (Bean)repository.getMessageCount(roomno);
						Integer messageCount = bean.getCount();
						Integer notReadMessage = messageCount - readCount;
						List<Bean> receiverForTitle = (List<Bean>)repository.getReceiverForTitle(roomno);
						bean=(Bean)repository.getLastMessage(roomno);
						bean.setCount(notReadMessage);
						totalCount += bean.getCount();
						
							if(receiverForTitle.size()==1){
								String receiverno2=Integer.toString(receiverForTitle.get(0).getMemno());
								receiverForTitle.get(0).setReceiverno(receiverno2);
								if(!receiverForTitle.get(0).getPhoto().equals("https://avatars.githubusercontent.com/u/7775019?v=2")){
									String[] photoArr5 = {"../img/userimages/", receiverForTitle.get(0).getPhoto()};
									String senderPhoto3 = concatenate(photoArr5);
									receiverForTitle.get(0).setPhoto(senderPhoto3);
								}
								bean.setReceiverno(receiverForTitle.get(0).getReceiverno());
								bean.setPhoto(receiverForTitle.get(0).getPhoto());
								bean.setName(receiverForTitle.get(0).getName());
							}
							else if(receiverForTitle.size()==2){
								for(int i=0; i<receiverForTitle.size(); i++){
									if(receiverForTitle.get(i).getMemno().equals(memNo7)){
										continue;
									}
									String receiverno2=Integer.toString(receiverForTitle.get(i).getMemno());
									bean.setReceiverno(receiverno2);
									bean.setName(receiverForTitle.get(i).getName());
									if(!receiverForTitle.get(i).getPhoto().equals("https://avatars.githubusercontent.com/u/7775019?v=2")){
										String[] photoArr5 = {"../img/userimages/", receiverForTitle.get(i).getPhoto()};
										String senderPhoto3 = concatenate(photoArr5);
										bean.setPhoto(senderPhoto3);
									}else{
										bean.setPhoto(receiverForTitle.get(i).getPhoto());
									}
								}
								
							}else{
								String receiversName2 = "";
								String receiversNo2 = "";
								for(int i=0; i<receiverForTitle.size(); i++){
									if(receiverForTitle.get(i).getMemno().equals(memNo7) ){
										continue;
									}
									
									if(i<receiverForTitle.size()-1 ){
										String[] receiverArr2 = {receiverForTitle.get(i).getName(), ", "};
										String receiverName2 = concatenate(receiverArr2);
										receiversName2 += receiverName2;
										
										String receiverno2 = Integer.toString(receiverForTitle.get(i).getMemno());
										String[] receivernoArr2 = {receiverno2, ","};
										String receiverNo2 = concatenate(receivernoArr2);
										receiversNo2 += receiverNo2;
									}
								}
								receiversName2 += receiverForTitle.get(receiverForTitle.size()-1).getName();
								String receiverno2 = Integer.toString(receiverForTitle.get(receiverForTitle.size()-1).getMemno());
								receiversNo2 += receiverno2;
								bean.setReceiverno(receiversNo2);
								bean.setName(receiversName2);
								bean.setPhoto("../img/userimages/default-group-image.gif");
						}
						
							messageTitle.add(bean);
							
					}
					bean.setObject1(messageTitle);
					bean.setTotalCount(totalCount);
					
					res=bean;
					
				break;
				
				case "messageRoom" :
					SimpleDateFormat day2 = new SimpleDateFormat("yyyyMMdd");
					long todaytime2 = 0;
					String today2, messageDay2, beforeMessageDay2 = null;
					Integer totalCount2 = 0;
					Integer memNo8=(Integer)(session.getAttribute("memNo"));
					Integer roomno2 = Integer.parseInt(request.getParameter("roomno"));
					bean.setMemno(memNo8);
					bean.setRoomno(roomno2);
					Bean maxMessageno = (Bean)repository.getMaxMessageno(bean);
					if(maxMessageno==null){
						List<Bean> messagenos = (List<Bean>)repository.getMessagenos(roomno2);
						for(Bean messageno : messagenos){
							messageno.setMemno(memNo8);
							messageno.setRoomno(roomno2);
							repository.readMessage(messageno);
						}
					}else{
						maxMessageno.setRoomno(roomno2);
						List<Bean> messagenos = (List<Bean>)repository.getMessagenosFromLast(maxMessageno);
						for(Bean messageno : messagenos){
							messageno.setMemno(memNo8);
							messageno.setRoomno(roomno2);
							repository.readMessage(messageno);
						}
					}
					
					List<Bean> roomnos2 = (List<Bean>)repository.getRoomno(memNo8);
					for(Bean roomno : roomnos2){
						roomno.setMemno(memNo8);
						bean = (Bean)repository.getReadCount(roomno);
						Integer readCount = bean.getCount();
						bean = (Bean)repository.getMessageCount(roomno);
						Integer messageCount = bean.getCount();
						Integer notReadMessage = messageCount - readCount;
						totalCount2 += notReadMessage;
					}
					
					List<Bean> getMessages = (List<Bean>)repository.getMessage(roomno2);
					
					for(int i=0; i<getMessages.size(); i++){
						if(!getMessages.get(i).getPhoto().equals("https://avatars.githubusercontent.com/u/7775019?v=2")){
							String[] photoArr6 = {"../img/userimages/", getMessages.get(i).getPhoto()};
							String senderPhoto4 = concatenate(photoArr6);
							getMessages.get(i).setPhoto(senderPhoto4);
						}
						getMessages.get(i).setMemno(memNo8);
						getMessages.get(i).setRoomno(roomno2);
					}
					
					todaytime2 = System.currentTimeMillis();
					today2 =  day2.format(new Date(todaytime2));
					List<Bean> messageDates2 = (List<Bean>)repository.getMessageDate(roomno2);
					for(int i=0; i<messageDates2.size(); i++){
						messageDay2 = day2.format(messageDates2.get(i).getDate());
						if(i==0){
							if(today2.equals(messageDay2)){
								getMessages.get(i).setToday(true);
							}else{
								getMessages.get(i).setToday(false);
							}
						}else{
							beforeMessageDay2 = day2.format(messageDates2.get(i-1).getDate());
							if(today2.equals(messageDay2) && !today2.equals(beforeMessageDay2)){
								getMessages.get(i).setToday(true);
							}else{
								getMessages.get(i).setToday(false);
							}
						}
					}
					
					
					bean.setObject1(getMessages);
					bean.setRoomno(roomno2);
					bean.setName(request.getParameter("name"));
					bean.setReceiverno(request.getParameter("receiverno"));
					bean.setTotalCount(totalCount2);
					
					
					
					
					res=bean;
					
				break;
				
				case "followerInvitationByLeader" :
					Integer memNo12=(Integer)(session.getAttribute("memNo"));
					bean.setMemno(memNo12);
					bean.setGroupno(Integer.parseInt(request.getParameter("groupno")));
					bean.setInvitetype('F');
					bean.setIsalarmed(false);
					String receivers = request.getParameter("receiverno");
					String[] receiver2 = receivers.split(",");
					for(int i=0; i<receiver2.length; i++){
						bean.setReceiver(Integer.parseInt(receiver2[i]));
						repository.sendInvitation(bean);
					}
					
					res="success";
					
				break;
				
				case "LeaderInvitationByLeader" :
					Integer memNo13=(Integer)(session.getAttribute("memNo"));
					bean.setMemno(memNo13);
					bean.setGroupno(Integer.parseInt(request.getParameter("groupno")));
					bean.setInvitetype('L');
					bean.setIsalarmed(false);
					bean.setReceiver(Integer.parseInt(request.getParameter("receiver")));
					
					repository.sendInvitation(bean);
					
					
					res="success";
					
				break;
				
				case "groupInvitation" :
					Integer memNo14=(Integer)(session.getAttribute("memNo"));
					List<Bean> invitations = (List<Bean>)repository.getInvitation(memNo14);
					for(int i=0; i<invitations.size(); i++){
						if(!invitations.get(i).getPhoto().equals("https://avatars.githubusercontent.com/u/7775019?v=2")){
							String[] photoArr = {"../img/userimages/", invitations.get(i).getPhoto()};
							String senderPhoto = concatenate(photoArr);
							invitations.get(i).setPhoto(senderPhoto);
						}
					}
					
					res = invitations;
				break;
				
				case "invitationAccept" :
					Integer memNo15=(Integer)(session.getAttribute("memNo"));
					bean.setMemno(memNo15);
					bean.setGroupno(Integer.parseInt(request.getParameter("groupno")));
					bean.setInvitno(Integer.parseInt(request.getParameter("invitno")));
					Bean isJoinedGroup = (Bean)repository.isJoinedGroup(bean);
					
					if(isJoinedGroup != null){
						if(isJoinedGroup.getLevel() == bean.getInvitetype() 
							&& bean.getInvitetype() == 'F'){
							res="이미 그룹에 가입되어 있습니다 ";
							
						}else if(isJoinedGroup.getLevel() == bean.getInvitetype()
							&& bean.getInvitetype() == 'L'){
							res="이미 리더입니다";
							
						}else{
							repository.leaderAccept(bean);
							repository.deleteGetInvitation(bean);
							res="리더가 되었습니다";
						}
						
					}else{
						repository.followerAccept(bean);
						repository.deleteGetInvitation(bean);
						res="그룹에 가입되었습니다";
						
					}
									
				break;
				
				case "deleteGetInvitation" :
					bean.setInvitno(Integer.parseInt(request.getParameter("invitno")));
					repository.deleteGetInvitation(bean);
					res="삭제되었습니다";
				break;
				
				case "joinRequest" :
					Integer memNo16=(Integer)(session.getAttribute("memNo"));
					bean.setMemno(memNo16);
					bean.setGroupno(Integer.parseInt(request.getParameter("groupno")));
					bean.setIsalarmed(false);
					bean.setReceiver(Integer.parseInt(request.getParameter("creator")));
					
					repository.sendJoinRequest(bean);
					res="가입 요청이 되었습니다";
					
				break;
		
				case "joinRequestPage" :
					Integer memNo17=(Integer)(session.getAttribute("memNo"));
					bean.setMemno(memNo17);
					bean.setGroupno(Integer.parseInt(request.getParameter("groupno")));
					System.out.println(bean.getMemno());
					System.out.println(bean.getGroupno());
					List<Bean> requests = (List<Bean>)repository.getJoinRequest(bean);
					for(int i=0; i<requests.size(); i++){
						if(!requests.get(i).getPhoto().equals("https://avatars.githubusercontent.com/u/7775019?v=2")){
							String[] photoArr = {"../img/userimages/", requests.get(i).getPhoto()};
							String requesterPhoto = concatenate(photoArr);
							requests.get(i).setPhoto(requesterPhoto);
						}
					}
					res=requests;
				break;
				
				case "requestAccept" :
					Integer memNo18=(Integer)(session.getAttribute("memNo"));
					bean.setMemno(memNo18);
					bean.setGroupno(Integer.parseInt(request.getParameter("groupno")));
					bean.setReqno(Integer.parseInt(request.getParameter("reqno")));
					bean.setRequester(Integer.parseInt(request.getParameter("requester")));
					bean.setLevel('F');
					
					Bean isJoinedGroup2 = (Bean)repository.isJoinedGroup2(bean);
					
					if(isJoinedGroup2 == null){
					repository.requestAccept(bean);
					repository.deleteGetRequest(bean);
					bean.setAccept(false);
					}else{
						bean.setAccept(true);
					}
						
					
					List<Bean> requests2 = (List<Bean>)repository.getJoinRequest(bean);
					for(int i=0; i<requests2.size(); i++){
						if(!requests2.get(i).getPhoto().equals("https://avatars.githubusercontent.com/u/7775019?v=2")){
							String[] photoArr = {"../img/userimages/", requests2.get(i).getPhoto()};
							String requesterPhoto = concatenate(photoArr);
							requests2.get(i).setPhoto(requesterPhoto);
						}
					}
					bean.setObject1(requests2);
					
					res=bean;
					
					
				break;
				
				case "deleteGetRequest" :
					Integer memNo19=(Integer)(session.getAttribute("memNo"));
					bean.setReqno(Integer.parseInt(request.getParameter("reqno")));
					repository.deleteGetRequest(bean);
					
					bean.setMemno(memNo19);
					bean.setGroupno(Integer.parseInt(request.getParameter("groupno")));
					
					List<Bean> requests3 = (List<Bean>)repository.getJoinRequest(bean);
					for(int i=0; i<requests3.size(); i++){
						if(!requests3.get(i).getPhoto().equals("https://avatars.githubusercontent.com/u/7775019?v=2")){
							String[] photoArr = {"../img/userimages/", requests3.get(i).getPhoto()};
							String requesterPhoto = concatenate(photoArr);
							requests3.get(i).setPhoto(requesterPhoto);
						}
					}
					
					res=requests3;
					
				break;
				
				case "invitationAcceptFromAlarm" :
					Integer memNo20=(Integer)(session.getAttribute("memNo"));
					bean.setMemno(memNo20);
					bean.setGroupno(Integer.parseInt(request.getParameter("groupno")));
					bean.setInvitno(Integer.parseInt(request.getParameter("invitno")));
					Bean isJoinedGroup3 = (Bean)repository.isJoinedGroup(bean);
					
					if(isJoinedGroup3 != null){
						if(isJoinedGroup3.getLevel() == bean.getInvitetype() 
							&& bean.getInvitetype() == 'F'){
							res="이미 그룹에 가입되어 있습니다 ";
							
						}else if(isJoinedGroup3.getLevel() == bean.getInvitetype()
							&& bean.getInvitetype() == 'L'){
							res="이미 리더입니다";
							
						}else{
							repository.leaderAccept(bean);
							repository.deleteGetInvitation(bean);
							res="리더가 되었습니다";
						}
						
					}else{
						repository.followerAccept(bean);
						repository.deleteGetInvitation(bean);
						res="그룹에 가입되었습니다";
						
					}
				break;
				
				case "requestAcceptFromAlarm":
					Integer memNo21=(Integer)(session.getAttribute("memNo"));
					bean.setMemno(memNo21);
					bean.setGroupno(Integer.parseInt(request.getParameter("groupno")));
					bean.setReqno(Integer.parseInt(request.getParameter("reqno")));
					bean.setRequester(Integer.parseInt(request.getParameter("requester")));
					bean.setLevel('F');
					
					Bean isJoinedGroup4 = (Bean)repository.isJoinedGroup2(bean);
					
					if(isJoinedGroup4 == null){
					repository.requestAccept(bean);
					repository.deleteGetRequest(bean);
					res="가입요청이 승인되었습니다";
					}else{
						res="이미 가입되었습니다";
					}
				break;
				
				case "memberRemove" :
					repository.groupWithdraw(bean);
					res="탈퇴 처리 되었습니다";
				break;
				
				case "viewAnnounce" :
					Integer startIndex = 0;
					Integer size = 3;
					Bean countAll = (Bean)repository.countAllAnnounce(bean);
					Integer totalSize = countAll.getTotalCount()/size;
					if(countAll.getTitleno()%size != 0){
						++totalSize;
					}
					Integer pageSize = totalSize/size;
					if(totalSize/size != 0){
						++pageSize;
					}
					bean.setStartIndex(startIndex);
					bean.setSize(size);
					
					bean.setObject1(repository.getAnnounce(bean));
					bean.setTotalSize(totalSize);
					bean.setPageSize(pageSize);
					res=bean;
				break;
				
				case "changeViewAnnounce":
					Integer startIndex2 = (Integer.parseInt(request.getParameter("index"))*3)-3;
					bean.setStartIndex(startIndex2);
					bean.setSize(3);
					bean.setObject1(repository.getAnnounce(bean));
					res=bean;
				break;
	
		/*
		 * 개발자 장윤용 끝 
		 */
		
		
		/*
		 * 개발자 홍성호
		 */
		
		case "groupAdmin" :			
			boolean chkLogged = Boolean.parseBoolean(request.getParameter("sessionGo"));
			System.out.println("groupAdmin_________"+chkLogged);
			if(chkLogged==true){
				bean.setGroupno((Integer)session.getAttribute("FirstGroup"));
			}else{			
				bean.setGroupno(Integer.parseInt(request.getParameter("groupNo")));
			}			
				bean.setObject1(repository.getGroupAdmin(bean.getGroupno()));
				bean.setObject2(repository.getGroupAdminTitle(bean.getGroupno()));
				bean.setTotalPage(repository.getGroupAdminTitleTotal(bean.getGroupno()));
			    res = bean;							
		break;
		
		case "groupAdminTitle_submit" :				
				String deletelist = request.getParameter("deletelist");
				
				String[] lists = deletelist.split(",");
			
				System.out.println("groupAdminTitle_submit_output : "+lists.length);	
				System.out.println("groupAdminTitle_submit_output : "+lists[0]);	

				
				if(lists[0]!=""){
					System.out.println("groupAdminTitle_submit_output_success : "+lists[0]);
					for(int i=0; i<lists.length; i++){
						System.out.println("groupAdminTitle_submit_output : "+lists[i]);	

						 //bring the delete list from mysql
						 //using Parameter : titleno
						ArrayList<Bean> deletelists 
						= (ArrayList<Bean>)(repository.getDeleteDetailList(Integer.parseInt(lists[i])));
						
						for(Bean beanz : deletelists){
						
						// Delete the contents and GitHub Repository of specific titleno 
						// using Parameter : contentno, git_id, git_pwd
												
						// Using Github API 
						try {
						if(!beanz.getGit_repository().equals("none")){
							github = new RtGithub(beanz.getGit_id(),beanz.getGit_pwd());
							Coordinates.Simple repositoryToBeRemoved = new Coordinates.Simple(beanz.getGit_id(),beanz.getGit_repository());
							github.repos().remove(repositoryToBeRemoved);
						}
					    } catch (IOException e) {}	
						
						//Remove Content data related to specific title from the DB 
						repository.getDeleteSpecific(beanz.getContentno());
						repository.getDeleteContentno(beanz.getContentno());
						}						
						
						//Remove data from the title table 
						repository.getDeleteTitleno(Integer.parseInt(lists[i]));
						github = null;
					}
				}			 	
				
			    if(fd.getList()!=null){
				for(Bean beanz : fd.getList()){
					repository.groupAdminTitle_submit(beanz);
				}
			    }
				
				if(fd.getList2()!=null){
				for(Bean beanz : fd.getList2()){
					beanz.setGroupno(bean.getGroupno());
					repository.groupAdminTitle_add(beanz);
					repository.groupAdminTitle_order(beanz.getTitleno());
				}
				}
				
				res=bean.getGroupno();
			break;
			
		case "groupAdmin_deleteIndividuals" :
				System.out.println("groupAdmin_deleteIndividuals_output : "+bean.getContentno());
				bean = (Bean)repository.getDeleteContentData(bean.getContentno());
				System.out.println("groupAdmin_deleteIndividuals_output : "+bean.getContentno()+" "+bean.getGit_id()+" "+bean.getGit_pwd()+" "+bean.getGit_repository());
				
				repository.getDeleteSpecific(bean.getContentno());
				repository.getDeleteContentno(bean.getContentno());
				
				try {
					if(!bean.getGit_repository().equals("none")){
						github = new RtGithub(bean.getGit_id(),bean.getGit_pwd());
						Coordinates.Simple repositoryToBeRemoved = new Coordinates.Simple(bean.getGit_id(),bean.getGit_repository());
						github.repos().remove(repositoryToBeRemoved);
					}
				} catch (IOException e) {}	
		break;
			
		case "box" :
			System.out.println("box_activated");
		break;		
		
		case "groupAdmin_displayContent" :
			 res = repository.groupAdmin_displayContent(Integer.parseInt(request.getParameter("titleno")));			
		break;
		
		case "groupAdmin_searchUsers" :
			 String name ="%";
			 name+=request.getParameter("name");
			 name+="%";
			 System.out.println("groupAdmin_searchUsers_output : "+name);
			 res = repository.groupAdmin_searchUsers(request.getParameter("name"));
		break;
		
		case "groupAdmin_memDataValidation" :
			 	   bean.setEmail(request.getParameter("email"));
			 	   bean.setMemno((Integer)repository.groupAdmin_memDataValidation(request.getParameter("email")));
			 	   bean.setCount(Integer.parseInt(request.getParameter("count")));
			 res = bean;
		break;
		
		case "memberChange_btn_submit":
			  for(Bean beanz : fd.getList()){
				  beanz.setGroupno(bean.getGroupno());
				  beanz.setInviterno(bean.getMemno());
				  repository.memberChange_btn_submit(beanz);
			  }
			  res=bean.getGroupno();			  
		break;
		
		case "personal_groupInvited" :
			  System.out.println("personal_groupInvited_output : "+session.getAttribute("memNo"));
			  res = repository.personal_groupInvited((Integer)session.getAttribute("memNo"));
		break;
		
		case "personal_answerInvite":
			  System.out.println("personal_answerInvite_output : "+
		      request.getParameter("invitno")+
		      " : "+request.getParameter("memno")+
		      " : "+request.getParameter("groupno"));	
			  
			  Bean beanz = new Bean();
			  beanz.setMemno(Integer.parseInt(request.getParameter("memno")));
			  beanz.setGroupno(Integer.parseInt(request.getParameter("groupno")));
			  beanz.setInvitno(Integer.parseInt(request.getParameter("invitno")));
			  repository.personal_answerInvite(beanz);
			  repository.personal_groupInvited_delete(beanz.getInvitno());
	    break;		
	    
		case "groupChange_btn_submit" :
			  System.out.println("groupChange_btn_submit_output : "+bean.getGroupno());
			  repository.groupChange_btn_submit(bean);
			  res=bean.getGroupno(); 
		break;
		
		
		/*
		 * 개발자 홍성호 끝 
		 */
		}
		return res;
	}
	
	
	/*
	 * 개발자 장윤용 
	 */
	
	public static String concatenate(String[] str){
		String result = new String();
		for (int i = 0; i < str.length; i++) {
			result = result.concat(str[i]);
		}      
		return result;
	}
	
	/*
	 * 개발자 장윤용 끝 
	 */
	
	
	@RequestMapping
	String view(@PathVariable("service") String service){
		for(String redirect : redirects){
			if(redirect.equals(service)){
				return "/main/personal/groupAdmin_submit";
			}
		}		
		return "/main/personal/"+service;
	}	
}
