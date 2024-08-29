#include <vector>
#include <iostream>
using namespace std;

class MajorityElement
{
public:
  int majorityElement(vector<int> &nums)
  {
    int majority_count = nums.size() / 2;
    while (true)
    {
      int candidate = nums[rand() % nums.size()];
      if (countOccurences(nums, candidate) > majority_count)
      {
        return candidate;
      }
    }
  }

private:
  int countOccurences(vector<int> &nums, int num)
  {
    int count = 0;
    for (int cur : nums)
    {
      if (num == cur)
        count++;
    }
    return count;
  }
};

int main()
{
  MajorityElement m;

  vector<int>
      test1 = {3,
               2,
               3};

  cout << m.majorityElement(test1);

  vector<int> test2 = {2,
                       2,
                       1,
                       1,
                       1,
                       2,
                       2};

  cout << m.majorityElement(test2);
  return 0;
}