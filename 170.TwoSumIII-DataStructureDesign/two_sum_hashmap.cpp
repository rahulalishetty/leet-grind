#include <unordered_map>
using namespace std;

class TwoSum
{
private:
  unordered_map<int, int> items;

public:
  TwoSum() {}

  void add(int number)
  {
    if (items.find(number) != items.end())
    {
      items[number]++;
    }
    else
    {
      items[number] = 1;
    }
  }

  bool find(int value)
  {
    for (const auto &item : items)
    {
      int complement = value - item.first;

      if (complement != item.first)
      {
        if (items.find(complement) != items.end())
        {
          return true;
        }
      }
      else
      {
        if (item.second > 1)
        {
          return true;
        }
      }
    }
    return false;
  }
};