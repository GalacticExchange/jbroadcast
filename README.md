### Reliable Broadcast

Party:

**uses** UDP, *ECDSA*  

**has** all public keys of other parties

**has** its own private key

 - wait message from client  
 - receive message, sign  
 - send signed message to client
 - receive all signs from client -> check -> commit 

---
 
Message should implement some sort of protocol.  

Message(GexPacket) consists of [HEADER(8 bytes), COMMAND(2 bytes), DATA]:
 - header = [INDEX(2 bytes), LENGTH(2 bytes), NONCE(4 bytes)]
 - command = {"sg", "ch"} // sign, check
 - data
 