# 607. Sales Person

## Table: SalesPerson

| Column Name     | Type    |
| --------------- | ------- |
| sales_id        | int     |
| name            | varchar |
| salary          | int     |
| commission_rate | int     |
| hire_date       | date    |

- `sales_id` is the **primary key**.
- Each row contains information about a **salesperson**, including salary, commission rate, and hire date.

---

## Table: Company

| Column Name | Type    |
| ----------- | ------- |
| com_id      | int     |
| name        | varchar |
| city        | varchar |

- `com_id` is the **primary key**.
- Each row represents a **company** and the city where it is located.

---

## Table: Orders

| Column Name | Type |
| ----------- | ---- |
| order_id    | int  |
| order_date  | date |
| com_id      | int  |
| sales_id    | int  |
| amount      | int  |

- `order_id` is the **primary key**.
- `com_id` is a **foreign key** referencing `Company.com_id`.
- `sales_id` is a **foreign key** referencing `SalesPerson.sales_id`.
- Each row represents an **order**, including the company involved, the salesperson who made the sale, and the order amount.

---

## Problem

Write a SQL query to find the **names of all salespersons who did NOT have any orders related to the company named `"RED"`**.

### Requirements

- Identify the company with the name `"RED"`.
- Find orders associated with that company.
- Identify the salespersons involved in those orders.
- Return the names of **all other salespersons who never had an order with `"RED"`**.

Return the result table in **any order**.

---

## Example

### Input

#### SalesPerson table

| sales_id | name | salary | commission_rate | hire_date  |
| -------- | ---- | ------ | --------------- | ---------- |
| 1        | John | 100000 | 6               | 4/1/2006   |
| 2        | Amy  | 12000  | 5               | 5/1/2010   |
| 3        | Mark | 65000  | 12              | 12/25/2008 |
| 4        | Pam  | 25000  | 25              | 1/1/2005   |
| 5        | Alex | 5000   | 10              | 2/3/2007   |

#### Company table

| com_id | name   | city     |
| ------ | ------ | -------- |
| 1      | RED    | Boston   |
| 2      | ORANGE | New York |
| 3      | YELLOW | Boston   |
| 4      | GREEN  | Austin   |

#### Orders table

| order_id | order_date | com_id | sales_id | amount |
| -------- | ---------- | ------ | -------- | ------ |
| 1        | 1/1/2014   | 3      | 4        | 10000  |
| 2        | 2/1/2014   | 4      | 5        | 5000   |
| 3        | 3/1/2014   | 1      | 1        | 50000  |
| 4        | 4/1/2014   | 1      | 4        | 25000  |

---

## Output

| name |
| ---- |
| Amy  |
| Mark |
| Alex |

---

## Explanation

From the `Orders` table:

- Order **3** → `sales_id = 1` → **John** sold to company **RED**.
- Order **4** → `sales_id = 4` → **Pam** sold to company **RED**.

So the salespersons who **did have orders with RED** are:

- John
- Pam

All other salespersons **did not sell to RED**, so they are included in the result:

- Amy
- Mark
- Alex
