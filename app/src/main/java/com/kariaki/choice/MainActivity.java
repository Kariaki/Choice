package com.kariaki.choice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kariaki.choice.contactsync.ContactData;
import com.kariaki.choice.contactsync.ContactSync;
import com.kariaki.choice.model.CloudPost;
import com.kariaki.choice.model.database.ChoiceViewModel;
import com.kariaki.choice.model.database.entities.ChoiceUser;
import com.kariaki.choice.model.database.entities.Contact;
import com.kariaki.choice.model.database.entities.ContactPost;
import com.kariaki.choice.model.database.entities.MyChatChannel;
import com.kariaki.choice.model.Post;
import com.kariaki.choice.model.UserDetail;
import com.kariaki.choice.model.UserInformation;
import com.kariaki.choice.notification.PushNotificationService;
import com.kariaki.choice.ui.camera.CameraFragment;
import com.kariaki.choice.ui.chat.ChatPage;
import com.kariaki.choice.ui.mainpage.adapters.HomeViewPagerAdapter;
import com.kariaki.choice.ui.mainpage.adapters.MakePostAdapter;
import com.kariaki.choice.ui.mainpage.bottomfragments.BottomMakePost;
import com.kariaki.choice.ui.mainpage.ChatFrament;
import com.kariaki.choice.ui.mainpage.pages.Chat;
import com.kariaki.choice.ui.mainpage.pages.Home;
import com.kariaki.choice.ui.mainpage.pages.MyProfile;
import com.kariaki.choice.ui.mainpage.pages.Notification;
import com.kariaki.choice.ui.mainpage.pages.notifications.MyContent;
import com.kariaki.choice.ui.mainpage.SearchPage;
import com.kariaki.choice.ui.mainpage.util.MainFunctions;
import com.kariaki.choice.ui.makepost.GalleryItem;
import com.kariaki.choice.ui.makepost.ImagePost;
import com.kariaki.choice.ui.makepost.VideoPost;
import com.kariaki.choice.ui.post.PostInfo;
import com.kariaki.choice.ui.post.PostTypes;
import com.kariaki.choice.ui.settings.entities.PrivacySettings;
import com.kariaki.choice.ui.settings.Settings;
import com.kariaki.choice.ui.util.ConnectionState;
import com.kariaki.choice.ui.util.Functions;
import com.kariaki.choice.ui.util.Gallery;
import com.kariaki.choice.ui.util.LastCheck;
import com.kariaki.choice.ui.util.PostLifeSpans;
import com.kariaki.choice.ui.util.Theme;
import com.kariaki.choice.ui.util.time.TimeFactor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    RecyclerView galleryList;

    private List<Fragment> pages = new ArrayList<>();
    private ViewPager2 pageNavigation;
    private HomeViewPagerAdapter adapter;

    private BottomNavigationView bottomNavigation;

    private ImageView pageOptions;
    MakePostAdapter makePostAdapter;
    DatabaseReference userList = FirebaseDatabase.getInstance().getReference("users");
    DatabaseReference myContacts;

    String[] allRequiredPermisions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.CAMERA};
    ChoiceViewModel choiceViewModel;
    String myPhoneNumber;
    DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
    DatabaseReference connection;
    CircleImageView mainActivityProfileImage;
    private String folder = "post";

    ProgressBar progress;
    private DatabaseReference postFolder = FirebaseDatabase.getInstance().getReference(folder);
    private BadgeDrawable badges;

    NotificationManagerCompat managerCompat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        myPhoneNumber = new UserInformation(this).getMainUserID();
        viewByID();
        bottomMakePost.setShowMarkings(false);
        initialize();
        choiceViewModel = ChoiceViewModel.getInstance(getApplication());
        adapter.setPages(pages);
        pageNavigation.setAdapter(adapter);
        loadProfileImage();
        loadPrivacySettings();


        managerCompat = NotificationManagerCompat.from(this);
        choiceOperations();
        reference = FirebaseStorage.getInstance().getReference("post");
        badges = bottomNavigation.getOrCreateBadge(R.menu.bottom_menu);


        AccountID accountID = AccountID.getInstance(this);

        myContacts = userList.child(accountID.getPhoneNumber()).child("people");

        if (!allPermissionGranted()) {
            requestNeededPermision();

        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    updateContacts();
                }
            }).start();
        }

        loadTheme();

        connection = userList.child(myPhoneNumber).child("Connection");
        connection();
        clickListeners();
        try {
            uploadPost();
        } catch (IOException e) {


            e.printStackTrace();
        }


        notificationBadge = bottomNavigation.getOrCreateBadge(R.id.notification);
        chatBadge = bottomNavigation.getOrCreateBadge(R.id.menuChat);
        chatBadge.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        chatBadge.setBadgeTextColor(getResources().getColor(R.color.whiteGreen));
        chatBadge.setNumber(0);
        chatBadge.setVisible(false);

        notificationBadge.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        notificationBadge.setBadgeTextColor(getResources().getColor(R.color.whiteGreen));
        notificationBadge.setNumber(0);
        notificationBadge.setVisible(false);
        networkRequest();
        countNotification();
        countChatBadge();
        Intent intent = new Intent(this, PushNotificationService.class);
        intent.putExtra("phone", myPhoneNumber);
        startService(intent);
        pageNavigation.setUserInputEnabled(false);


    }

    boolean amConnected = false;

    BadgeDrawable notificationBadge;
    BadgeDrawable chatBadge;


    @Override
    protected void onStart() {
        super.onStart();

        loadTheme();
        // viewByID();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTheme();
        // viewByID();

    }

    private int lifeSpan;
    UploadTask uploadTask;
    StorageReference reference;
    byte[] sendingBytes;

    private void uploadPost() throws IOException {
        Intent intent = getIntent();
        Intent postData = getIntent();
        int postType = postData.getIntExtra("postType", -1);

        long postTime = System.currentTimeMillis();

        if (postType != -1) {
            String[] ulrs = intent.getStringArrayExtra("images");
            String caption = intent.getStringExtra("postCaption");
            int type = intent.getIntExtra("postType", postType);
            lifeSpan = postData.getIntExtra("post duration", PostLifeSpans.MAX);
            long createLifeSpan = TimeFactor.createDefaultLifeSpan(System.currentTimeMillis(), lifeSpan);

            boolean willRepeat = postData.getBooleanExtra("repeat", false);
            boolean allowPrivateComment = postData.getBooleanExtra("allow private comment", true);

            PostInfo info = new PostInfo(createLifeSpan, willRepeat, allowPrivateComment);
            if (postType == PostTypes.SINGLE_POST || postType == PostTypes.MERGED_POST) {

                if (ulrs.length == 1) {
                    //dotsLoader.setVisibility(View.VISIBLE);
                    //logo.setVisibility(View.INVISIBLE);
                    Uri uri = Uri.fromFile(new File(ulrs[0]));
                    String path = ulrs[0];
                    String fileName = String.valueOf(System.currentTimeMillis()) + Functions.fileExtension(ulrs[0]);
                    CloudPost initialCloudPost = new CloudPost("", myPhoneNumber, caption, "", type, postTime, createLifeSpan, "");

                    reference = reference.child(String.valueOf(postTime)).child(fileName);

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90 - 5, outputStream);
                    sendingBytes = outputStream.toByteArray();

                    uploadTask = reference.putBytes(sendingBytes);

                    uploadSingleFile(uploadTask, reference, initialCloudPost, info);

                } else {
                    reference = reference.child(String.valueOf(postTime));
                    String[] innerUrl = intent.getStringArrayExtra("images");
                    CloudPost initialCloudPost = new CloudPost("", myPhoneNumber, caption, "", type, postTime, createLifeSpan, "");

                    for (String url : ulrs) {
                        //dotsLoader.setVisibility(View.VISIBLE);
                        //logo.setVisibility(View.INVISIBLE);
                        Uri uri = Uri.fromFile(new File(url));
                        String path = ulrs[0];
                        String fileName = String.valueOf(System.currentTimeMillis()) + Functions.fileExtension(url);

                        StorageReference thisFile = reference.child(String.valueOf(postTime)).child(fileName);

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90 - 5, outputStream);
                        sendingBytes = outputStream.toByteArray();

                        uploadTask = thisFile.putBytes(sendingBytes);
                        uploadFile(initialCloudPost, info, uploadTask, thisFile, innerUrl.length);


                    }

                }
            } else {

                String[] newUrl = intent.getStringArrayExtra("images");
                Uri uri = Uri.fromFile(new File(newUrl[0]));
                String fileName = String.valueOf(System.currentTimeMillis()) + Functions.fileExtension(newUrl[0]);
                CloudPost initialCloudPost = new CloudPost("", myPhoneNumber, caption, "", type, postTime, createLifeSpan, "");

                reference = reference.child(String.valueOf(postTime)).child(fileName);
                uploadTask = reference.putFile(uri);
                uploadSingleFile(uploadTask, reference, initialCloudPost, info);


            }

        }
    }

    private void networkRequest() {


        choiceViewModel = ChoiceViewModel.getInstance(getApplication());
        new Thread(new Runnable() {
            @Override
            public void run() {
                MainFunctions.fetchBadgeCounter(myPhoneNumber);
                MainFunctions.checkForNotificationExistence(myPhoneNumber);
                MainFunctions.fetchChats(myPhoneNumber, choiceViewModel);

                fetchPost();
            }
        }).start();

    }

    private void countChatBadge() {
        DatabaseReference myChats = userList.child(myPhoneNumber);
        DatabaseReference badge = myChats.child("chat_badge");
        badge.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    chatBadge.setNumber((int) dataSnapshot.getChildrenCount());
                    chatBadge.setVisible(true);
                } else {
                    chatBadge.setVisible(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void uploadSingleFile(UploadTask uploadTask, StorageReference reference, CloudPost cloudPost, PostInfo info) {

        uploadTask.addOnProgressListener(
                new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        progress.setVisibility(View.VISIBLE);
                        int currentProgress = (int) ((taskSnapshot.getBytesTransferred() * 100) / taskSnapshot.getTotalByteCount());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            progress.setProgress(currentProgress, true);
                        } else {
                            progress.setProgress(currentProgress);
                        }

                    }
                })
                .

                        addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        cloudPost.setPOST_URL(cloudPost.getPOST_URL() + uri.toString() + ",");
                                        uploadToFolder(cloudPost, info);
                                        progress.setVisibility(View.GONE);
                                        Toast.makeText(MainActivity.this, "Posted", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        });

    }

    List<String> urlsList = new ArrayList<>();

    private void uploadFile(CloudPost cloudPost, PostInfo info, UploadTask uploadTask, StorageReference reference, int length) {

        uploadTask.addOnProgressListener(
                new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        progress.setVisibility(View.VISIBLE);
                        progress.setIndeterminate(true);

                    }
                })
                .

                        addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        urlsList.add(uri.toString());

                                        if (urlsList.size() == length) {
                                            String url = printUrls(urlsList);
                                            cloudPost.setPOST_URL(url);
                                            uploadToFolder(cloudPost, info);
                                        }
                                    }
                                });
                            }
                        });

    }

    private void uploadToFolder(CloudPost post, PostInfo info) {

        postFolder = FirebaseDatabase.getInstance().getReference(folder);
        DatabaseReference newPost = postFolder.push();
        post.setPostID(newPost.getKey());
        Post thepost = new Post(post.getPostID(), post.getOwnerID(), post.getPOST_CAPTION(), post.getPOST_URL(),
                post.getPOST_TYPE(), post.getPOST_TIME(), false, post.getOwnerID(), false);

        newPost.setValue(thepost)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        progress.setVisibility(View.GONE);
                        bottomNavigation.setSelectedItemId(R.id.notification);
                        pageNavigation.setCurrentItem(1, true);
                        Intent intent = getIntent();
                        intent.putExtra("postType", -1);

                        setIntent(intent);

                    }
                });
        DatabaseReference postData = newPost.child("postData");
        postData.setValue(info);
        registerMyPost(post);


    }

    private String printUrls(List<String> urlsList) {
        String output = "";
        for (String url : urlsList) {
            output = output + url + ",";
        }
        return output;
    }

    private void registerMyPost(CloudPost cloudPost) {
        DatabaseReference myPost = userList.child(myPhoneNumber).child("post").child(cloudPost.getPostID());

        LastCheck lastCheck = new LastCheck(cloudPost.getPOST_TIME(), 0);
        myPost.setValue(lastCheck);

    }

    private void connection() {
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);

                if (connected) {
                    ConnectionState connectionState = new ConnectionState(0, true);
                    connection.setValue(connectionState);
                    amConnected = true;

                } else {
                    amConnected = false;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ConnectionState connectionState = new ConnectionState(System.currentTimeMillis(), false);
        connection.setValue(connectionState);
        connection.onDisconnect().setValue(connectionState);
    }


    private void loadPrivacySettings() {
        userList.child(myPhoneNumber).child("Privacy")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            PrivacySettings privacySettings = new PrivacySettings(true, Settings.EVERYONE, false);
                            dataSnapshot.getRef().setValue(privacySettings);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this, "Unable to fetch data from server", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void choiceOperations() {

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHome:
                        pageNavigation.setCurrentItem(0, false);

                        break;
                    case R.id.notification:
                        pageNavigation.setCurrentItem(1, false);

                        break;
                    case R.id.menuChat:
                        pageNavigation.setCurrentItem(2, false);

                        break;
                    case R.id.search:
                        Intent intent = new Intent(MainActivity.this, SearchPage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, SEARCH);

                        break;
                    case R.id.empty:


                        pageNavigation.setCurrentItem(3);
                        break;
                }
                return true;
            }
        });
        pageNavigation.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigation.setSelectedItemId(R.id.menuHome);
                        break;
                    case 1:
                        bottomNavigation.setSelectedItemId(R.id.notification);
                        break;
                    case 2:
                        bottomNavigation.setSelectedItemId(R.id.menuChat);

                        break;
                }
            }
        });


    }

    String path = "";
    List<GalleryItem> images = new ArrayList<>();
    BottomMakePost bottomMakePost = new BottomMakePost();

    private void clickListeners() {

        bottomMakePost.setShowMarkings(false);

        bottomMakePost.setShowDone(false);


    }

    private void openGallery() {
        if (allPermissionGranted()) {

            Gallery gallery = new Gallery(MainActivity.this);

            images = gallery.getImages();
            bottomMakePost.setImages(images);
            makePostAdapter = new MakePostAdapter(this, images);
            makePostAdapter.setMarkedImages(markedImages);
            makePostAdapter.setOnItemClickListener(makePostItemClickLister);
            makePostAdapter.setShowMarkings(false);
            bottomMakePost.setAdapter(makePostAdapter);
            //   bottomMakePost.show(getSupportFragmentManager(), String.valueOf(System.currentTimeMillis()));
            FragmentManager manager = getSupportFragmentManager();

            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fragmentChanger, bottomMakePost).addToBackStack(null).commit();

        }
    }

    int SEARCH = 1515;
    ArrayList<String> markedImages = new ArrayList<>();
    Intent sendingIntent;
    MakePostAdapter.OnclickListener makePostItemClickLister = new MakePostAdapter.OnclickListener() {

        @Override
        public void onClickImage(int position, ImageView marker) {

            switch (images.get(position).getFileType()) {

                case 1:

                    sendingIntent = new Intent(MainActivity.this, ImagePost.class);
                    markedImages.add(images.get(position).getFileURL());

                    sendingIntent.putStringArrayListExtra("yourImages", markedImages);
                    sendingIntent.putExtra("scrollLocation", position);
                    startActivity(sendingIntent);
                    markedImages.clear();

                    break;
                case 2:

                    Intent videoIntent = new Intent(MainActivity.this, VideoPost.class);
                    videoIntent.putExtra("videoURL", images.get(position).getFileURL());
                    startActivity(videoIntent);

                    break;
            }


        }
    };


    String currentChat;

    private void startCamera() {
        CameraFragment cameraFragment = new CameraFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.mainRoot, cameraFragment).commit();
    }

    private void initialize() {
        pages.add(new Home());

        Notification notification = new Notification();
        notification.setListener(new MyContent.onClickListener() {
            @Override
            public void clickListener() {
                openGallery();
            }
        });
        MyProfile myProfile = new MyProfile();

        pages.add(notification);

        Chat chat = new Chat();
        chat.setOnOpenPageListener(new Chat.OpenPageListener() {
            @Override
            public void openPage(MyChatChannel chatChannel, Drawable drawable, String text) {


                Intent intent = new Intent(MainActivity.this, ChatPage.class);

                intent.putExtra("phone number", chatChannel.getChatID());

                intent.putExtra("channelType", chatChannel.getChannelType());
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("name", text);
                startActivity(intent);
                //overridePendingTransition(android.R.anim.slide_in_left,0);


              /*
                ChatFrament chatFrament;
                chatFrament = new ChatFrament();
                chatFrament.setChatID(chatChannel.getChatID());
                chatFrament.setChat_type(chatChannel.getChannelType());
                chatFrament.setName(text);
                pageOptions.setVisibility(View.INVISIBLE);
                frameLayout = findViewById(R.id.fragmentChanger);

                manager.beginTransaction()

                        .addToBackStack(chatChannel.getChatID())
                        .add(R.id.fragmentChanger, chatFrament, chatChannel.getChatID())
                        .commit();


                displayFragment(drawable, chatChannel, text);
  */

            }
        });
        pages.add(chat);
        pages.add(myProfile);


        adapter = new HomeViewPagerAdapter(this);

    }

    private void displayFragment(Drawable drawable, MyChatChannel chatChannel, String text) {
        ChatFrament chatFrament = (ChatFrament) manager.findFragmentByTag(chatChannel.getChatID());
        if (chatFrament == null) {
            chatFrament = new ChatFrament();
            chatFrament.setChat_type(chatChannel.getChannelType());
            chatFrament.setName(text);
            chatFrament.setChatID(chatChannel.getChatID());

            frameLayout = findViewById(R.id.fragmentChanger);
            currentChat = chatChannel.getChatID();


            FragmentTransaction transaction = manager.beginTransaction();

            transaction.addToBackStack(chatChannel.getChatID())
                    .add(R.id.fragmentChanger, chatFrament, chatChannel.getChatID())
                    .commit();


        }
    }

    CoordinatorLayout mainRoot;
    ImageView logo;
    String channel;

    private void viewByID() {
        pageNavigation = findViewById(R.id.mainActivityRoot);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        pageOptions = findViewById(R.id.pageOptions);
        mainRoot = findViewById(R.id.mainRoot);
        logo = findViewById(R.id.logo);

        choiceName = findViewById(R.id.choiceName);
        mainActivityToolbar = findViewById(R.id.mainActivityToolbar);
        mainActivityAppbar = findViewById(R.id.mainActivityAppbar);
        progress = findViewById(R.id.progress);
        mainActivityProfileImage = findViewById(R.id.mainActivityProfileImage);
    }

    public void openProfilePage(View view) {


        //pageNavigation.setCurrentItem(3);
        bottomNavigation.setSelectedItemId(R.id.empty);


    }

    private void loadProfileImage() {
        DatabaseReference myDetaiils = userList.child(myPhoneNumber);
        myDetaiils.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserDetail detail = dataSnapshot.getValue(UserDetail.class);
                ChoiceUser choiceUser = new ChoiceUser(detail.getUsername(), detail.getPhone(), detail.getDisplayName(), detail.getAbout(), detail.getProfileURL());
                choiceViewModel.insertChoiceUserToLocaldb(choiceUser);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        choiceViewModel.getChoiceUser(new UserInformation(this).getPhoneNumber()).observe(this, new Observer<List<ChoiceUser>>() {
            @Override
            public void onChanged(List<ChoiceUser> choiceUsers) {
                if(!choiceUsers.isEmpty()) {
                    Glide.with(getApplicationContext()).load(choiceUsers.get(0).getUserImageUrl()).
                            diskCacheStrategy(DiskCacheStrategy.DATA)
                            .thumbnail(0.4f)
                            .into(mainActivityProfileImage);
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();


        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

            getSupportFragmentManager().popBackStack();


        } else {


            if (pageNavigation.getCurrentItem() != 0) {
                pageNavigation.setCurrentItem(0, false);
                bottomNavigation.setSelectedItemId(R.id.menuHome);
            } else {

                finish();
            }
        }


    }

    private boolean allPermissionGranted() {

        for (String permission : allRequiredPermisions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void requestNeededPermision() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, allRequiredPermisions, PackageManager.PERMISSION_GRANTED);

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SEARCH && resultCode == RESULT_CANCELED || requestCode == SEARCH && resultCode == RESULT_OK) {
            pageNavigation.setCurrentItem(0);
            bottomNavigation.setSelectedItemId(R.id.menuHome);

        }
    }

    private void updateContacts() {


        resetPhoneNumber();

        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("users");
        DocumentReference myDocument = collectionReference.document(myPhoneNumber);

        for (String phone : newPhoneNumber) {
            if (!myPhoneNumber.equals(phone)) {
                userList.child(phone)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    UserDetail detail = dataSnapshot.getValue(UserDetail.class);
                                    String messageURL = detail.getProfileURL();
                                    if (messageURL != null) {
                                        int position = newPhoneNumber.indexOf(phone);
                                        String name = contactName.get(position);
                                        Contact contact = new Contact(name, phone);
                                        if (!phone.equals(myPhoneNumber)) {

                                            ChoiceUser choiceUser = new ChoiceUser(detail.getUsername(), contact.getPhoneNumber(),
                                                    contact.getContactName(), detail.getAbout(), detail.getProfileURL());
                                            myContacts.child(phone).setValue(choiceUser);


                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        }


    }

    List<ContactData> contactData = new ArrayList<>();
    List<String> contactName = new ArrayList<>();
    List<String> newPhoneNumber = new ArrayList<>();


    TextView choiceName;
    Toolbar mainActivityToolbar;
    AppBarLayout mainActivityAppbar;

    public void setTextColors(TextView[] text, int currentTheme) {

        switch (currentTheme) {
            case Theme.LIGHT:
                changeTextColors(text, R.color.textContext);

                mainRoot.setBackgroundColor(getResources().getColor(R.color.whiteGreen));

                mainActivityAppbar.setBackgroundColor(getResources().getColor(R.color.whiteGreen));

                mainActivityToolbar.setBackgroundColor(getResources().getColor(R.color.whiteGreen));
                bottomNavigation.setBackgroundColor(getResources().getColor(R.color.whiteGreen));
                break;
            case Theme.DEEP:
                changeTextColors(text, R.color.whiteGreen);
                mainRoot.setBackgroundColor(getResources().getColor(R.color.darkGreen));

                mainActivityAppbar.setBackgroundColor(getResources().getColor(R.color.darkGreen));
                mainActivityToolbar.setBackgroundColor(getResources().getColor(R.color.darkGreen));


                bottomNavigation.setBackgroundColor(getResources().getColor(R.color.darkGreen));
                break;

        }
    }


    private void loadTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("themes", MODE_PRIVATE);

        int currentTheme = sharedPreferences.getInt("currentTheme", Theme.DEEP);

        TextView[] textViews = {choiceName};
        setTextColors(textViews, currentTheme);

    }

    private void changeTextColors(TextView[] textViews, int color) {
        for (TextView text : textViews) {
            text.setTextColor(getResources().getColor(color));
        }

    }


    private void resetPhoneNumber() {
        contactData = ContactSync.getContacts(this);
        for (int i = 0; i < contactData.size(); i++) {

            String oldValue = contactData.get(i).getPhone();

            if (oldValue.length() >= 10) {
                String newvalue = Functions.numberAnalyzer(oldValue);
                newPhoneNumber.add(newvalue);
                contactName.add(contactData.get(i).getName());
            }

        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        int currentPage = pageNavigation.getCurrentItem();
        outState.putInt("last_page", currentPage);

        outState.putInt("selectedID", bottomNavigation.getSelectedItemId());

    }

    @Override
    protected void onStop() {
        super.onStop();
        // fetchPostForNotification();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        int lastPage = savedInstanceState.getInt("last_page", 0);
        adapter.setPages(pages);
        pageNavigation.setAdapter(adapter);
        pageNavigation.setCurrentItem(lastPage);

        int selectedItem = savedInstanceState.getInt("selectedID", R.id.menuHome);
        bottomNavigation.setSelectedItemId(selectedItem);
    }

    @Override
    protected void onPause() {

        super.onPause();

    }


    private void collectPost(DatabaseReference snap, String id, String fromID) {
        postFolder = FirebaseDatabase.getInstance().getReference("post");
        postFolder.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    CloudPost cloudPost = dataSnapshot.getValue(CloudPost.class);
                    checkToConfirmTake(dataSnapshot.getRef().child("postData")
                            , dataSnapshot.getRef(), cloudPost, fromID);

                } else {
                    snap.removeValue();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            List<Post> thePost = choiceViewModel.getPost(dataSnapshot.getKey());
                            if (thePost == null && !thePost.isEmpty()) {

                                choiceViewModel.deletePost(thePost.get(0));

                            }
                        }
                    });


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    SharedPreferences preferences;

    private void checkToConfirmTake(DatabaseReference postInfo,

                                    DatabaseReference snapshot, CloudPost cloudPost, String fromID) {


        preferences = getSharedPreferences("post", MODE_PRIVATE);
        boolean allow = preferences.getBoolean("show only contact post", false);

        postInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                if (dataSnapshot1.exists()) {
                    PostInfo postInfo = dataSnapshot1.getValue(PostInfo.class);
                    Post post = new Post(cloudPost.getPostID(), cloudPost.getOwnerID(), cloudPost.getPOST_CAPTION(),
                            cloudPost.getPOST_URL(), cloudPost.getPOST_TYPE(),
                            cloudPost.getPOST_TIME()
                            , postInfo.getPostIsOnRepeat(), fromID, postInfo.isAllowPrivateComment());

                    if (TimeFactor.lifeSpanTimer(postInfo.getPostLifeSpan())) {

                        String url = post.getPOST_URL();

                        if (url != null) {
                            if (!url.isEmpty()) {
                                Functions.deleteFile(url);
                            }
                        }
                        //dataSnapshot.getRef().removeValue();
                        snapshot.getRef().removeValue();
                        choiceViewModel.deletePost(post);


                    } else {

                        if (!allow) {
                            choiceViewModel.insertPost(post);

                        } else {
                            if (post.isRepeat()) {
                                userList.child(myPhoneNumber).child("people")
                                        .child(post.getOwnerID())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    choiceViewModel.insertPost(post);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                        }
                        //myTimeLine.child(post.getPostID()).setValue(post);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //UI function
    private void countNotification() {


        DatabaseReference badgeCount = userList.child(myPhoneNumber).child("badge");
        badgeCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    notificationBadge.setNumber((int) dataSnapshot.getChildrenCount());
                    notificationBadge.setVisible(true);
                } else {
                    notificationBadge.setVisible(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void fetchPost() {

        userList.child(myPhoneNumber).child("people")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ContactPost contactPost = snapshot.getValue(ContactPost.class);
                                assert contactPost != null;
                                if (!contactPost.isMute() && !contactPost.isBlockUser()) {
                                    userList.child(snapshot.getKey()).child("post")
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        for (DataSnapshot postID : dataSnapshot.getChildren()) {

                                                            collectPost(postID.getRef(), postID.getKey(), snapshot.getKey());
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    RelativeLayout frameLayout;

    FragmentManager manager = getSupportFragmentManager();
    FragmentTransaction transaction = manager.beginTransaction();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, PushNotificationService.class);


    }


}