var x = 42
print("outside x is " + x)
var message = (function (x) {
    return function () {
        print("in message fn, x is " + x);
    }
})(x);

message();
x = 12;
print("outside x is " + x)
message();

var phone = "222-333 3333";

var ex = "^[0-9]{3}[\\s\\-][0-9]{3}[\\s\\-][0-9]{4}$";

if (phone.match(ex)) {
    print("matches");
} else {
    print("no match");
}