/*
 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 */

/*
 *  TestPushNotificationApp is used to test the Push Notification
 */

//package org.jivesoftware.openfire.plugin.rest;
//
//import java.util.List;
//
//import org.apache.log4j.BasicConfigurator;
//import org.json.JSONException;
//
//import javapns.Push;
//import javapns.communication.exceptions.CommunicationException;
//import javapns.communication.exceptions.KeystoreException;
//import javapns.notification.PushNotificationPayload;
//import javapns.notification.PushedNotification;
//import javapns.notification.ResponsePacket;
//
//public class TestPushNotificationApp {
//
//	public static void main(String[] args) {
//		BasicConfigurator.configure();
//		try {
//			PushNotificationPayload payload = PushNotificationPayload.complex();
//			payload.addAlert("test notification");
//			payload.addBadge(1);
//			payload.addSound("default");
//			payload.addCustomDictionary("id", "1");
//			payload.addCustomDictionary("content-available", 1);
//			System.out.println(payload.toString());
//			List<PushedNotification> NOTIFICATIONS =  Push.payload(payload,
//					"/home/dhaval_patel/Downloads/GoToChatsCertificate.p12", "Brain@2020", false,
//					"50c05bbc8a8dbd59779499c74a434e567f88c0304729f3891ebab8ad6debfb71");
//
//			for (PushedNotification NOTIFICATION : NOTIFICATIONS) {
//				if (NOTIFICATION.isSuccessful()) {
//					/* APPLE ACCEPTED THE NOTIFICATION AND SHOULD DELIVER IT */
//					System.out
//							.println("PUSH NOTIFICATION SENT SUCCESSFULLY TO: " + NOTIFICATION.getDevice().getToken());
//					/* STILL NEED TO QUERY THE FEEDBACK SERVICE REGULARLY */
//				} else {
//					String INVALIDTOKEN = NOTIFICATION.getDevice().getToken();
//					/* ADD CODE HERE TO REMOVE INVALIDTOKEN FROM YOUR DATABASE */
//
//					/* FIND OUT MORE ABOUT WHAT THE PROBLEM WAS */
//					Exception THEPROBLEM = NOTIFICATION.getException();
//					THEPROBLEM.printStackTrace();
//
//					/* IF THE PROBLEM WAS AN ERROR-RESPONSE PACKET RETURNED BY APPLE, GET IT */
//					ResponsePacket THEERRORRESPONSE = NOTIFICATION.getResponse();
//					if (THEERRORRESPONSE != null) {
//						System.out.println(THEERRORRESPONSE.getMessage());
//					}
//				}
//			}
//		} catch (CommunicationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (KeystoreException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//}
 
/*
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
 */
