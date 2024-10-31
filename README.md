# Torwin
Minecraft reverse proxy (from scratch) that can intercept packages.

(Only works for login)

## Wikki

### Packet Structure

| Field Name | Field Type | Notes                                           |
|------------|------------|-------------------------------------------------|
| Length     | VarInt     | Length of Packet ID + Data                      |
| Packet ID  | VarInt     | Corresponds to protocol id                      |
| Data       | ByteArray  | Depends on the connection state and packet ID   |

### Login

1. C→S: Handshake with Next State set to 2 (login)
2. C→S: Login Start
3. S→C: Encryption Request
4. Client auth (if enabled)
5. C→S: Encryption Response
6. Server auth (if enabled)
7. Both enable encryption
8. S→C: Set Compression (optional)
9. S→C: Login Success
10. C→S: Login Acknowledged

Minecraft Protocol Wiki https://wiki.vg/Protocol
