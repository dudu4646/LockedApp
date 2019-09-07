package packet.com.lockedappproject.models;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class FireBase {

    private static FirebaseDatabase db = FirebaseDatabase.getInstance();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static DatabaseReference ref = null, userRef = null, houseRef = null, lockRef = null,reqRef=null;
    private static User user = null;
    private static ArrayList<Lock> userLocks;
    private static ArrayList<House> userHouses;
    private static HashMap<String,Req> requests;
    private static HashMap<String, User> nicks;
    private static ArrayList<UpdateHouseData> updateHouse = new ArrayList<>();
    private static ArrayList<UpdateLockData> updateLocks = new ArrayList<>();
    private static  ArrayList<UpdateRequests> updateRequests = new ArrayList<>();

    //LISTENERS:
    private static ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            user = dataSnapshot.getValue(User.class);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    private static ChildEventListener lockListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Lock lock = dataSnapshot.getValue(Lock.class);
            if (user.lockList.contains(lock.id)) {
                userLocks.add(lock);
                Collections.sort(userLocks);
                for (UpdateLockData u : updateLocks)
                    u.Notify();
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Lock lock = dataSnapshot.getValue(Lock.class);

            if (lock.admin.contains(mAuth.getUid()) || lock.notAdmin.contains(mAuth.getUid())) {
                int i;
                for (i = 0; i < userLocks.size() && !userLocks.get(i).id.equalsIgnoreCase(lock.id); i++)
                    ;
                if (i < userLocks.size()) {
                    userLocks.set(i, lock);
                }
            } else {
                int i;
                for (i = 0; i < userLocks.size() && !userLocks.get(i).id.equalsIgnoreCase(lock.id); i++)
                    ;
                if (i < userLocks.size())
                    userLocks.remove(i);
            }
            Collections.sort(userLocks);
            for (UpdateLockData u : updateLocks)
                u.Notify();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            Lock lock = dataSnapshot.getValue(Lock.class);
            int i;
            for (i = 0; i < userLocks.size() && !userLocks.get(i).id.equalsIgnoreCase(lock.id); i++)
                ;
            if (i < userLocks.size()) {
                userLocks.remove(i);
                for (UpdateLockData u : updateLocks)
                    u.Notify();
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    private static ChildEventListener houseListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            House house = dataSnapshot.getValue(House.class);
            if (user.houseList.contains(house.id)) {
                userHouses.add(house);
                Collections.sort(userHouses);
                for (UpdateHouseData u : updateHouse)
                    u.Notify();
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            House house = dataSnapshot.getValue(House.class);
            if (house.admin.contains(mAuth.getUid())) {
                int i;
                for (i = 0; i < userHouses.size() && !userHouses.get(i).id.equalsIgnoreCase(house.id); i++)
                    ;
                if (i < userHouses.size()) {
                    userHouses.set(i, house);
                }
            } else {
                int i;
                for (i = 0; i < userHouses.size() && !userHouses.get(i).id.equalsIgnoreCase(house.id); i++)
                    ;
                if (i < userHouses.size())
                    userLocks.remove(i);
            }
            Collections.sort(userHouses);
            for (UpdateHouseData u : updateHouse)
                u.Notify();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            House house = dataSnapshot.getValue(House.class);
            int i;
            for (i = 0; i < userHouses.size() && !userHouses.get(i).id.equalsIgnoreCase(house.id); i++)
                ;
            if (i < userHouses.size()) {
                userHouses.remove(i);
                for (UpdateHouseData u : updateHouse)
                    u.Notify();
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    private static ChildEventListener reqListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            System.out.println("testing ---> reqListener added");
            Req req = dataSnapshot.getValue(Req.class);
            if(req.getToUsers().contains(getUid()))
                requests.put(dataSnapshot.getKey(),req);
            for(UpdateRequests u:updateRequests)
                u.Notify(requests.size());
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            System.out.println("testing ---> reqListener changed");
            if(requests.containsKey(dataSnapshot.getKey())){
                Req r = dataSnapshot.getValue(Req.class);
                if(!r.getToUsers().contains(getUid()))
                    requests.remove(dataSnapshot.getKey());
                for(UpdateRequests u:updateRequests)
                    u.Notify(requests.size());
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            System.out.println("testing ---> reqListener removed");
            if(requests.containsKey(dataSnapshot.getKey()))
                requests.remove(dataSnapshot.getKey());
            for(UpdateRequests u:updateRequests)
                u.Notify(requests.size());
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    //GETTERS:
    //get current user
    public static User getUser() {
        return user;
    }

    //get user ID
    public static String getUid() {
        return mAuth.getUid();
    }

    //get all the user houses
    public static ArrayList<House> getHouses() {
        return userHouses;
    }

    //get all the user locks
    public static ArrayList<Lock> getLockes() {
        return userLocks;
    }

    //get locks from single house
    public static ArrayList<Lock> getLockFromList(String list) {
        String[] locks = list.split(",");
        ArrayList<Lock> arr = new ArrayList<>();
        for (String s : locks) {
            for (Lock l : userLocks) {
                if (l.id.equalsIgnoreCase(s)) {
                    arr.add(l);
                }
            }
        }
        Collections.sort(arr);
        return arr;
    }

    //get single house by id/name
    public static House getOneHouse(String str) {
        for (House h : userHouses)
            if (h.id.equalsIgnoreCase(str) || (h.name.equalsIgnoreCase(str)))
                return h;
        return null;
    }

    //get single house by lock
    public static House getHousebyLock(String str) {
        for (House h : userHouses)
            if (h.locks.contains(str))
                return h;
        return null;
    }

    //get lock from list by id
    public static Lock getLockByStr(String id) {
        for (Lock lock : userLocks)
            if (lock.id.equalsIgnoreCase(id))
                return lock;
        return null;
    }

    //get user email from nick
    public static String getEmailFromNick(String nick) {
        for(String uId:nicks.keySet()){
            User user = nicks.get(uId);
            if (user.nick.equalsIgnoreCase(nick))
                return user.email;
        }
        return null;
    }

    //get Requests
    public static HashMap<String, Req> getRequests() {
        return requests;
    }

    //get another user
    public static User getAnotherUser(String key){
        if (nicks.containsKey(key))
            return nicks.get(key);
        return null;
    }

    //METHODS:
    //Sign up
    public static void signUp(final String user, String pass, final UpdateUi u) {
        mAuth.signInWithEmailAndPassword(user, pass)
                .addOnCompleteListener((Activity) u, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            if (userRef != null)
                                userRef.removeEventListener(userListener);
                            userRef = db.getReference("users").child(getUid());
                            userRef.addValueEventListener(userListener);
                            download();
                            u.Success();

                        } else {
                            // If sign in fails, display a message to the user.
                            u.Failed(task.getException());
                        }
                    }
                });
    }

    //Download data
    private static void download() {
        userHouses = new ArrayList<>();
        userLocks = new ArrayList<>();
        requests = new HashMap<>();
        if (houseRef != null) {
            houseRef.removeEventListener(houseListener);
            lockRef.removeEventListener(lockListener);
        }
        houseRef = db.getReference("house");
        houseRef.addChildEventListener(houseListener);
        lockRef = db.getReference("locks");
        lockRef.addChildEventListener(lockListener);
        reqRef= db.getReference("req");
        reqRef.addChildEventListener(reqListener);

    }

    //download all the users nicks
    public static void getNicks() {
        nicks = new HashMap<>();
        ref = db.getReference("users");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User u = dataSnapshot.getValue(User.class);
                nicks.put(dataSnapshot.getKey(), u);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                nicks.put(dataSnapshot.getKey(),dataSnapshot.getValue(User.class));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                if (nicks.containsKey(dataSnapshot.getKey()))
                    nicks.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //checking if nick is taken
    public static boolean checkNick(String s) {
        return nicks.containsKey(s);
    }

    //add listener for houses updates
    public static void addToUpdateHouse(UpdateHouseData adapter) {
        int place = updateHouse.indexOf(adapter);
        if (place > -1)
            updateHouse.set(place, adapter);
        else
            updateHouse.add(adapter);
    }

    //remove listener for houses updates
    public static void removeFromUpdateHouse(UpdateHouseData adapter) {
        if (updateHouse.contains(adapter))
            updateHouse.remove(adapter);
    }

    //add listener for locks updates
    public static void addToUpdateLocks(UpdateLockData adapter) {
        int place = updateLocks.indexOf(adapter);
        if (place > -1)
            updateLocks.set(place, adapter);
        else
            updateLocks.add(adapter);
    }

    //add listener for requests updates
    public static void addToRequestsUpdates(UpdateRequests u){
        int place = updateRequests.indexOf(u);
        if (place>-1)
            updateRequests.set(place,u);
        else
            updateRequests.add(u);
    }

    //remove listener for Requests updates
    public static void removeFromRequestsUpdates(UpdateRequests u){
        System.out.println("testing remove request updates before = "+updateRequests.size());
//        if (updateRequests.contains(u))
        updateRequests.remove(u);
        System.out.println("testing remove request updates after = "+updateRequests.size());
    }

    //remove listener for locks updates
    public static void removeFromUpdateLock(UpdateLockData adapter) {
        if (updateLocks.contains(adapter))
            updateLocks.remove(adapter);
    }

    //changing lock status
    public static void changeLockTo(Lock lock, String status) {
        lock.status = status;
        ref = db.getReference("locks").child(lock.id);
        ref.setValue(lock);
    }

    //looking for specific lock in all DB
    public static void searchGeneralLock(final String lId, final FindLock cb) {
        System.out.println("testing ---> FireBase.searchGeneralLock() starts");
        houseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean found = false;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    House house = data.getValue(House.class);
                    if (house.locks.contains(lId)) {
                        found = true;
                        findLockInDB(lId, house, cb);
                    }
                }
                if (!found)
                    cb.notFound(lId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        System.out.println("testing ---> FireBase.searchGeneralLock() ends");
    }

    //deleting lock process
    public static void deleteLock(String lName, String hId) {
        //טיפול במנעול
        Lock lock = getLockByStr(lName);
        //אם היוזר אדמין
        if (lock.admin.contains(getUid())) {
            lock.admin = deleteUid(lock.admin);
            if (lock.admin.equalsIgnoreCase("")) {
                if (lock.notAdmin.equalsIgnoreCase("")) {
                    lockRef.child(lock.id).removeValue();
                }
                else {
                    lock = makeAdmin(lock);
                }
            }
        }
        else { //יוזר רגיל
            lock.notAdmin = deleteUid(lock.notAdmin);
            //עדכון המנעול ב- FB
            lockRef.child(lock.id).setValue(lock);
        }
        //בדיקה אם צריך למחוק את המנעול
        boolean flg = (lock.admin.equalsIgnoreCase(""))&&lock.notAdmin.equalsIgnoreCase("");

        //טיפול בבית - אם צריך למחוק ממנו את המנעול
        House house = getOneHouse(hId);
        ArrayList<String> arr;
        if (flg) {
            //בדיקה אם יש עוד מנעולים בבית
            arr = new ArrayList<>(Arrays.asList(house.locks.split(",")));
            arr.remove(lock.id);
            if (arr.size() == 0)
                //מחיקת הבית מ- FB
                houseRef.child(house.id).removeValue();
            else {
                //מחיקת המנעול מהבית ועדכון ב FB
                house.locks = buildStringFromList(arr);
                houseRef.child(house.id).setValue(house);
            }
        }

        //עדכון היוזר - מחיקת המנעול
        User user = getUser();
        arr = new ArrayList<>(Arrays.asList(user.lockList.split(",")));
        System.out.println("testing ---> FireBae - deleteLock() - עדכון יוזר - arr.size() "+arr.size());
        arr.remove(lock.id);
        user.lockList = (arr.size() > 0) ? buildStringFromList(arr) : "";
        //בדיקה אם צריך למחוק את הבית
        if (house.locks.equalsIgnoreCase("")) {
            arr = new ArrayList<>(Arrays.asList(user.houseList.split(",")));
            //מחיקת הבית מהרשומה ביוזר
            arr.remove(house.id);
            user.houseList = (arr.size() > 0) ? buildStringFromList(arr) : "";
        }
        //עדכון יוזר ב- FB
        userRef.setValue(user);
    }

    //adding new lock to House
    public static void addNewLock(Lock lock, House house) {
        User user = getUser();
        user.lockList = add_id_to_string(user.lockList, lock.id);
        house.locks = add_id_to_string(house.locks, lock.id);

        //בדיקה אם הבית חדש
        if (house.id == null || house.id.length() == 0) {
            ref = houseRef.push();
            house.id = ref.getKey();
            user.houseList = add_id_to_string(user.houseList,house.id);
        }
        //כתיבת המידע
        userRef.setValue(user);
        houseRef.child(house.id).setValue(house);
        lockRef.child(lock.id).setValue(lock);
    }

    //adding req to DB
    public static void AddReq(House house, Lock lock) {
        System.out.println("testing ---> FireBase.addReq() starts");
        reqRef.push().setValue(new Req(lock.id,house.id,getUid(),lock.admin));
    }

    //PRIVATE METHODS:
    //add id to string
    private static String add_id_to_string(String src, String id) {
        src += (src.length() > 0) ? "," + id : id;
//        ArrayList<String> arr = new ArrayList<>(Arrays.asList(src.split(",")));
//        Collections.sort(arr);
//        String s = "";
//        for (int i = 0; i < arr.size(); i++)
//            if (i == 0)
//                s = arr.get(i);
//            else
//                s += "," + arr.get(i);
//        return s;
        return buildStringFromList(Arrays.asList(src.split(",")));
    }

    //build string from list (sort + adding ",")
    private static String buildStringFromList(List<String> list) {
        Collections.sort(list);
        String s = "";
        for (int i = 0; i < list.size(); i++)
            if (i == 0)
                s = list.get(i);
            else
                s += "," + list.get(i);
        return s;
    }

    //finding lock in the all DB
    private static void findLockInDB(final String str, final House house, final FindLock cb) {
        System.out.println("testing -->   FireBase.findLockInDB():  house = " + house.name + " lock = " + str);
        lockRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Lock lock = data.getValue(Lock.class);
                    if (lock.id.equalsIgnoreCase(str)) {
                        cb.found(house, lock);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //remove uId from string
    private static String deleteUid(String str) {
        ArrayList<String> arr = new ArrayList<>(Arrays.asList(str.split(",")));
        arr.remove(getUid());
        // list.remove(getUid());
        if (arr.size() == 0)
            return "";
        return buildStringFromList(arr);
    }

    //make first not-admin --> admin
    private static Lock makeAdmin(Lock lock) {
        ArrayList<String> not = new ArrayList<>(Arrays.asList(lock.notAdmin.split(",")));
        String id = not.get(0);
        not.remove(0);
        lock.notAdmin = (not.size() > 0) ? buildStringFromList(not) : "";
        lock.admin = (lock.admin.length() > 0) ? add_id_to_string(lock.admin, id) : id;
        return lock;
    }

    //make specific user admin
    private static Lock makeAdmin(Lock lock, String uId) {
        return null;
    }

    public static String getReqNum() {
        return requests.size()>0?requests.size()+"":"";
    }

    //INTERFACES:
    public interface UpdateUi {
        void Success();

        void Failed(Exception e);
    }

    public interface UpdateHouseData {
        void Notify();
    }

    public interface UpdateLockData {
        void Notify();
    }

    public interface FindLock {
        void found(House house, Lock lock);

        void notFound(String lId);
    }

    public interface UpdateRequests {
        void Notify(int size);
    }
}
