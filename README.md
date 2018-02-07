### Verifiable Broadcast

Party:

**uses** UDP, ECDSA 

**has** all public keys of other parties

**has** its own private key

*n* - total nodes.
*t* - potential malicious nodes

 - wait message from client  
 - receive message, sign  
 - send signed message to client  
 - receive `(n+t+1)/2` signs from client -> check -> commit 


---  

### Reliable Broadcast

Party

**uses** UDP

*n* - total nodes.
*t* - potential malicious nodes

 - waits for message from client
 - sends **echo{message, nonce}** to other parties  
 - waits for echos:
    - received `(n+t+1)/2` **echo** messages and not sent **ready** -> send **ready{message, nonce}** to other parties
 - waits for readies:
    - received `t+1` **ready** messages and not sent **ready** -> send **ready{message, nonce}** to other parties
    - received `2t+1` **ready** messages -> commit 
 
