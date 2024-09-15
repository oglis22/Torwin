# Torwin
Minecraft reverse proxy (from scratch) that can intercept packages.

## Wikki

### Packet Structure

| Field Name | Field Type | Notes                                           |
|------------|------------|-------------------------------------------------|
| Length     | VarInt     | Length of Packet ID + Data                      |
| Packet ID  | VarInt     | Corresponds to protocol id                      |
| Data       | ByteArray  | Depends on the connection state and packet ID   |

### Login

C→S: Handshake with Next State set to 2 (login)
C→S: Login Start
S→C: Encryption Request
Client auth (if enabled)
C→S: Encryption Response
Server auth (if enabled)
Both enable encryption
S→C: Set Compression (optional)
S→C: Login Success
C→S: Login Acknowledged

Minecraft Protocol Wiki https://wiki.vg/Protocol
