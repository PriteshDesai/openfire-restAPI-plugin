/*
 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 */

/*
 *  APNsUtils is used to test the APNS VOIP Notification
 */

//package org.jivesoftware.openfire.plugin.rest;
//
//import java.io.File;
//import java.util.Date;
//import java.util.UUID;
//
//import com.turo.pushy.apns.ApnsClient;
//import com.turo.pushy.apns.ApnsClientBuilder;
//import com.turo.pushy.apns.DeliveryPriority;
//import com.turo.pushy.apns.PushNotificationResponse;
//import com.turo.pushy.apns.PushType;
//import com.turo.pushy.apns.util.SimpleApnsPushNotification;
//import com.turo.pushy.apns.util.concurrent.PushNotificationFuture;
//
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//
//public class APNsUtils {
//    private static ApnsClient apnsClient = null;
//    public static void main(String[] args) throws Exception {
//    //DeviceToken returned after registration of terminal devices such as IOS
//        String deviceToken ="4851751ca20cdec8c690936352ccda8162854f5e5bb3d62bcf267041054ce227";
////        String deviceToken = "74abd7d51c58a8db995fa53c3508a72481a9d4ad56e7ed1f7d12362f798a6906";
//        /**
//         * Use the voip push type for notifications that provide information about an incoming Voice-over-IP (VoIP)
//         * call. For more information, see Responding to VoIP Notifications from PushKit.
//         * If you set this push type, the apns-topic header field must use your app’s bundle ID with .voip
//         * appended to the end. If you’re using certificate-based authentication,
//         * you must also register the certificate for VoIP services.
//         * The topic is then part of the 1.2.840.113635.100.6.3.4 or 1.2.840.113635.100.6.3.6 extension.
//         */
//         //This is your subject, in most cases it is bundleId, voip needs to add .voip to bundleId. Corresponding to apns-topic in the document
//         //You can refer to https://developer.apple.com/documentation/usernotifications/setting_up_a_remote_notification_server/sending_notification_requests_to_apns?language=objc here
//      
//        String topic = "com.gotochats.app.voip";
//        String payload = "{ \"aps\": {\"alert\": \"test\", \"sound\": \"default\", \"content-available\" :1 ,\"badge\" :0} }";//,\"liguoxin\":\" liguoxin\" }";
//        //Effective time
//        Date invalidationTime= new Date(System.currentTimeMillis() + 60 * 60 * 1000L );
//        //Send strategy apns-priority 10 is immediate 5 is power saving
//        DeliveryPriority priority= DeliveryPriority.IMMEDIATE;
//        //Push method, mainly include alert, background, voip, replication, fileprovider, mdm
//        PushType pushType = PushType.VOIP;
//        //Push merge ID, the same apns-collapse-id will be merged in App
//        String collapseId= UUID.randomUUID().toString();
//        //apnsId is a unique identifier, if not passed, APNs will generate one for us
//        UUID apnsId = UUID.randomUUID();
//        //Construct a push message entity of APNs
//        SimpleApnsPushNotification msg = new SimpleApnsPushNotification(deviceToken,topic,payload,invalidationTime,priority,pushType,collapseId,apnsId);
//		//Start pushing
//        PushNotificationFuture<SimpleApnsPushNotification,PushNotificationResponse<SimpleApnsPushNotification>> future = getAPNSConnect().sendNotification(msg);
//        PushNotificationResponse<SimpleApnsPushNotification> response = future.get();
//        System.out.println(response.getRejectionReason());
//        //If success is true in the returned message, then it succeeds, otherwise it fails!
//        //Don't panic if it fails, there will be a reason for the failure in the rejectionReason field. Find the reason on the official website
//        //https://developer.apple.com/documentation/usernotifications/setting_up_a_remote_notification_server/handling_notification_responses_from_apns?language=objc
//        
//        System.out.println("------------->"+response);
//    }
//
//    public static ApnsClient getAPNSConnect() {
//
//        if (apnsClient == null) {
//            try {
//                /**
//                 * Development server: api.sandbox.push.apple.com:443
//                 * Production server: api.push.apple.com:443
//                 *
//                                   * The domain name api.development.push.apple.com cannot be found on Apple's official website now
//                 */
//
//                String SANDBOX_APNS_HOST = "api.sandbox.push.apple.com";
////                /Users/liguoxin/Desktop/p12/deve_push.p12
////                 /Users/liguoxin/Desktop/p12/distri_push.p12
//               //Four threads
//                EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
//                apnsClient = new ApnsClientBuilder().setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
//                        .setClientCredentials(new File("/home/dhaval_patel/Downloads/CertAPNSVoip.p12"),"Brain@2021").build();
//                        //.setConcurrentConnections(4).setEventLoopGroup(eventLoopGroup).build();
//
//
////                EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
////                apnsClient = new ApnsClientBuilder().setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
////                        .setSigningKey(ApnsSigningKey.loadFromPkcs8File(new File("/Users/liguoxin/Desktop/p12/deve_push.p8"),
//// "My temid", "My keyid"))
////                        .setConcurrentConnections(4).setEventLoopGroup(eventLoopGroup).build();
//            } catch (Exception e) {
//               // log.error("ios get pushy apns client failed!");
//                e.printStackTrace();
//            }
//        }
//
//        return apnsClient;
//
//    }
//}

 /*
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
 */