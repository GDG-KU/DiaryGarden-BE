Client token refresh (SDK-driven)

Overview
- This project delegates token refresh to the Firebase client SDK (web/mobile).
- Backend returns 401 (Unauthorized) when a protected endpoint is called without a valid ID token.
- Clients should detect 401 responses and use the SDK to refresh the ID token, then retry the request.

Recommended client behavior (Web, using Firebase JS SDK)
1. When making API calls, include the ID token in the Authorization header:
   Authorization: Bearer <idToken>

2. If the API responds with 401, force-refresh the ID token and retry once:
   firebase.auth().currentUser.getIdToken(true)
     .then((newIdToken) => {
       // retry the failed request with newIdToken
     })
     .catch((err) => {
       // handle error (user might need to re-authenticate)
     });

Notes for mobile (Android / iOS)
- The native Firebase SDKs handle token refresh automatically in many cases. If you see 401, call the SDK method to get a fresh token and retry.
  - Android: FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)...
  - iOS: user.getIDTokenForcingRefresh(true) ...

Server-side behavior
- Protected endpoints (e.g., /api/diaries/**) will return 401 when Authorization header is missing or token is invalid/expired.
- The server does not attempt to refresh tokens; this avoids holding or managing refresh tokens on the server and lets clients refresh via SDK.

Security considerations
- This approach is simpler but relies on client platform security. For web apps, avoid storing refresh tokens in localStorage; prefer guided SDK usage and short-lived ID tokens.
- If you later require stronger control (token revocation, server-managed sessions), consider implementing a server-side refresh flow.
