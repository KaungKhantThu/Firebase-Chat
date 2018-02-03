package xyz.kkt.firebasechat.data.models;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import xyz.kkt.firebasechat.FireChatApp;
import xyz.kkt.firebasechat.data.vo.ChatVO;
import xyz.kkt.firebasechat.data.vo.ConversationVO;
import xyz.kkt.firebasechat.data.vo.UserVO;
import xyz.kkt.firebasechat.events.RestApiEvents;
import xyz.kkt.firebasechat.utils.AppConstants;
import xyz.kkt.firebasechat.utils.ConfigUtils;


public class FireChatModel {

    private static final String CHAT = "chats";
    private static final String REGISTERED_USER = "registeredUsers";

    private static FireChatModel objectInstance;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private List<ConversationVO> mConversationList;
    private List<UserVO> mUserList;

    // private String currentUserId = "102412084351996807702";
    private String mPartnerId;
    private boolean found = false;

    private ConfigUtils mConfigUtils;

    private FireChatModel() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mConversationList = new ArrayList<>();
        mUserList = new ArrayList<>();
        mConfigUtils = FireChatApp.getConfigUtils();
    }

    public static FireChatModel getInstance() {
        if (objectInstance == null) {
            objectInstance = new FireChatModel();
        }
        return objectInstance;
    }

    public void startLoadingRegisteredUser(final Context context) {

        DatabaseReference fireChatDBR = FirebaseDatabase.getInstance().getReference();
        DatabaseReference registeredUserDBR = fireChatDBR.child(REGISTERED_USER);
        registeredUserDBR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<UserVO> userList = new ArrayList<>();
                for (DataSnapshot userDSS : dataSnapshot.getChildren()) {
                    UserVO user = userDSS.getValue(UserVO.class);
                    userList.add(user);
                }

                mUserList.addAll(userList);
                RestApiEvents.UserDataLoadedEvent event = new RestApiEvents.UserDataLoadedEvent(mUserList);
                EventBus.getDefault().post(event);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void startConversation(final String partnerId) {
        found = false;
        mPartnerId = partnerId;

        final DatabaseReference fireChatDBR = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference conversationDBR = fireChatDBR.child(CHAT);
        conversationDBR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ChatVO> chatList = new ArrayList<>();
                for (DataSnapshot chatDSS : dataSnapshot.getChildren()) {
                    ChatVO chat = chatDSS.getValue(ChatVO.class);
                    chatList.add(chat);
                }
                for (ChatVO chatVO : chatList) {
                    int i = 0;
                    for (UserVO user : chatVO.getUserList().values()) {
                        if (user.getUserId().equals(partnerId) && user.getUserId().equals(mConfigUtils.loadCurrentUserId())) {
                            found = true;
                        }
                    }
                }
                if (found = false) {
                    DatabaseReference userDBR = fireChatDBR.child(REGISTERED_USER).child(mConfigUtils.loadCurrentUserId());
                    userDBR.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final Map<String, UserVO> userList = new HashMap<>();

                            UserVO currentUser = dataSnapshot.getValue(UserVO.class);
                            userList.put(currentUser.getUserId(), currentUser);

                            Log.e("UID", userList.size() + "");

                            final DatabaseReference partnerDBR = fireChatDBR.child(REGISTERED_USER).child(partnerId);
                            partnerDBR.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    UserVO partnerUser = dataSnapshot.getValue(UserVO.class);
                                    userList.put(partnerUser.getUserId(), partnerUser);

                                    ChatVO chatVO = new ChatVO();
                                    chatVO.setChatId(mConfigUtils.loadCurrentUserId() + mPartnerId);
                                    chatVO.setStartedAt(new Date().toString());
                                    chatVO.setUserList(userList);
                                    conversationDBR.child(chatVO.getChatId()).setValue(chatVO);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    DatabaseReference chatDBR = FirebaseDatabase.getInstance().getReference();
                    final DatabaseReference conversatinDBR = chatDBR.child(CHAT).child(mConfigUtils.loadCurrentUserId() + mPartnerId);
                    conversatinDBR.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mConversationList = new ArrayList<>();
                            ChatVO chatVO = dataSnapshot.getValue(ChatVO.class);
                            mConversationList.addAll(chatVO.getConversations().values());
                            RestApiEvents.ChatDataLoadedEvent event = new RestApiEvents.ChatDataLoadedEvent(mConversationList);
                            EventBus.getDefault().post(event);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public boolean isUserAuthenticate() {
        return mFirebaseUser != null;
    }


    public void authenticateUserWithGoogleAccount(final GoogleSignInAccount signInAccount, final UserAuthenticateDelegate delegate) {
        Log.d(FireChatApp.LOG, "signInAccount Id :" + signInAccount.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(FireChatApp.LOG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.d(FireChatApp.LOG, "signInWithCredential", task.getException());
                            delegate.onFailureAuthenticate(task.getException().getMessage());
                        } else {
                            mFirebaseUser = mFirebaseAuth.getCurrentUser();
                            Log.d(FireChatApp.LOG, "signInWithCredential - successful");
                            delegate.onSuccessAuthenticate(signInAccount);

                            UserVO userVO = new UserVO();
                            userVO.setEmail(signInAccount.getEmail());
                            userVO.setCoverUrl(signInAccount.getPhotoUrl().toString());
                            userVO.setName(signInAccount.getDisplayName());
                            userVO.setProfileUrl(signInAccount.getPhotoUrl().toString());
                            userVO.setUserId(signInAccount.getId());
                            DatabaseReference fireChatDBR = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference registeredUserDBR = fireChatDBR.child(REGISTERED_USER);
                            registeredUserDBR.child(userVO.getUserId()).setValue(userVO);

                            //  currentUserId = signInAccount.getId();
                            mConfigUtils.saveCurrentUserId(signInAccount.getId());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(FireChatApp.LOG, "OnFailureListener : " + e.getMessage());
                        delegate.onFailureAuthenticate(e.getMessage());
                    }
                });
    }

    public void sendMessage(final String mPhotoUrl, final String message) {
        if (mPhotoUrl != null) {
            uploadFile(mPhotoUrl, new UploadFileCallback() {
                @Override
                public void onUploadSucceeded(String uploadedPaths) {
                    String image = uploadedPaths;
                    final ConversationVO conversationVO = new ConversationVO(mConfigUtils.loadCurrentUserId(), mConfigUtils.loadCurrentUserId() + new Date().toString(), message, mPhotoUrl, new Date().toString());
                    if (found = true) {
                        DatabaseReference chatDBR = FirebaseDatabase.getInstance().getReference();
                        final DatabaseReference conversatinDBR = chatDBR.child(CHAT).child(mConfigUtils.loadCurrentUserId() + mPartnerId);
                        conversatinDBR.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ChatVO value = dataSnapshot.getValue(ChatVO.class);
                                value.getConversations().put(conversationVO.getConversationId(), conversationVO);
                                conversatinDBR.setValue(value);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                }

                @Override
                public void onUploadFailed(String msg) {

                }
            });
        } else {
            final ConversationVO conversationVO = new ConversationVO(mConfigUtils.loadCurrentUserId(), mConfigUtils.loadCurrentUserId() + new Date().toString(), message, "", new Date().toString());
            if (found = true) {
                DatabaseReference chatDBR = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference conversatinDBR = chatDBR.child(CHAT).child(mConfigUtils.loadCurrentUserId() + mPartnerId);
                conversatinDBR.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ChatVO value = dataSnapshot.getValue(ChatVO.class);
                        value.getConversations().put(conversationVO.getConversationId(), conversationVO);
                        conversatinDBR.setValue(value);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    public void uploadFile(String fileToUpload, final UploadFileCallback uploadFileCallback) {
        Uri file = Uri.parse(fileToUpload);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference pathToUpload = storage.getReferenceFromUrl(AppConstants.FIRE_STOPRAGE_BUCKET);

        StorageReference uploadingFile = pathToUpload.child(file.getLastPathSegment()); // get file name
        UploadTask uploadTask = uploadingFile.putFile(file);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                uploadFileCallback.onUploadFailed(e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri uploadedImageUri = taskSnapshot.getDownloadUrl();
                uploadFileCallback.onUploadSucceeded(uploadedImageUri.toString());
            }
        });
    }

    public interface UploadFileCallback {
        void onUploadSucceeded(String uploadedPaths);

        void onUploadFailed(String msg);
    }

    public interface UserAuthenticateDelegate {
        void onSuccessAuthenticate(GoogleSignInAccount signInAccount);

        void onFailureAuthenticate(String errrorMsg);
    }

    public List<ConversationVO> getConversationList() {
        return mConversationList;
    }

    public List<UserVO> getmUserList() {
        return mUserList;
    }
}
