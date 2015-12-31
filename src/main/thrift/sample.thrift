namespace java com.force.ser

struct Item {
  1: i64 id,
  2: string content,
}

service ListItemService {
    void write(1:list<Item> items),
}