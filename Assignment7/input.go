package main

x int;
x = 10;

// Need to work on pass by reference
func passByReference(y *int) void {
    y = 5;
    Println(y);
}


passByReference(x, y, z);
passByReference(x);
Println(x);
