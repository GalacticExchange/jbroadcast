Linux buffer configuration (in bytes):  

`sudo sysctl -w net.core.rmem_default=31299200`  

TCP & UDP notes:

- Receiving data linux stack tuning(in depth): https://blog.packagecloud.io/eng/2016/06/22/monitoring-tuning-linux-networking-stack-receiving-data
  
- Sending data linux stack tuning(in depth): https://blog.packagecloud.io/eng/2017/02/06/monitoring-tuning-linux-networking-stack-sending-data/  
  
- RedHat article about network performance tuning: https://access.redhat.com/sites/default/files/attachments/20150325_network_performance_tuning.pdf  

  
  
- TCP vs UDP speed: 
    * https://stackoverflow.com/questions/47903/udp-vs-tcp-how-much-faster-is-it
    * https://unix.stackexchange.com/questions/122281/why-is-my-tcp-throughput-much-greater-than-udp-throughput  
    

- MTU packet size: https://stackoverflow.com/questions/1098897/what-is-the-largest-safe-udp-packet-size-on-the-internet