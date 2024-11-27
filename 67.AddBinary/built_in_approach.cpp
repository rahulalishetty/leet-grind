#include <bitset>
#include <string>

using namespace std;

class Builtin {
public:
  string addBinary(string a, string b) {
    int numA = stoi(a, nullptr, 2); // Convert binary string to integer
    int numB = stoi(b, nullptr, 2); // Convert binary string to integer
    int sum = numA + numB;          // Sum the integers
    if (sum == 0)
      return "0";
    string binary = bitset<32>(sum).to_string(); // Convert sum to binary string
    return binary.substr(binary.find('1'));
  }
};
