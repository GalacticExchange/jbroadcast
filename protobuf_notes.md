#### Types  
1. fixed32 = 1 byte key + 4 byte value
2. bytes =   
{  
  2 byte key + N byte value   
  OR   
  3 byte key + N byte value if N > 127  
}   
3. `repeated` and `map` are *optional* parameters  by default  
4. string = 2 bytes key

