rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /AppUsers/{userId} {
      allow read, write: if request.auth != null;
    }
  }
}
