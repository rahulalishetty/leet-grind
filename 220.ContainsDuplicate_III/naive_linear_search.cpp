#include <vector>
#include <algorithm>
using namespace std;

class LinearSearch
{
public:
  bool containsNearbyAlmostDuplicate(vector<int> &nums, int k, int t)
  {
    int n = nums.size();
    for (int i = 0; i < n; i++)
    {
      for (int j = max(i - k, 0); j < i; j++)
      {
        if (abs(nums[i] - nums[j]) <= t)
          return true;
      }
    }
    return false;
  }
};
