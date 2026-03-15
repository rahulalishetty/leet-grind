#include <string>
using namespace std;

class BruteForce {
public:
  string longestPalindrome(string s) {
    for (int length = s.size(); length > 0; length--) {
      for (int start = 0; start <= s.size() - length; start++) {
        if (check(s, start, start + length)) {
          return s.substr(start, length);
        }
      }
    }

    return "";
  }

private:
  bool check(string s, int i, int j) {
    int left = i;
    int right = j - 1;

    while (left < right) {
      if (s[left] != s[right]) {
        return false;
      }

      left++;
      right--;
    }

    return true;
  }
};
