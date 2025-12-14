Table users {
  id bigint [pk, increment]
  email varchar(255) [unique, not null]
  password varchar(255) [not null]
  name varchar(100) [not null]
  birth_date date
  gender varchar(10)
  address varchar(255)
  phone_number varchar(20)
  role varchar(20) [not null, default: 'USER']
  profile_image varchar(255)
  created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
  updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
  deleted_at timestamp
  is_active boolean [not null, default: true]
}

Table sellers {
  id bigint [pk, increment]
  business_name varchar(255) [not null]
  business_number varchar(20) [unique, not null]
  email varchar(255) [unique, not null]
  phone_number varchar(20) [not null]
  address varchar(255) [not null]
  payout_bank varchar(100) [not null]
  payout_account varchar(100) [not null]
  payout_holder varchar(100) [not null]
  created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
  updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
  deleted_at timestamp
  is_active boolean [not null, default: true]
}

Table categories {
  id bigint [pk, increment]
  name varchar(100) [unique, not null]
  description text
  parent_id bigint
  created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
  updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
}

Ref: categories.parent_id > categories.id [delete: set null]

Table books {
  id bigint [pk, increment]
  title varchar(255) [not null]
  author varchar(100) [not null]
  publisher varchar(100) [not null]
  summary text
  isbn varchar(20) [unique, not null]
  price decimal(10,2) [not null]
  publication_date date [not null]
  seller_id bigint
  categories text
  created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
  updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
  deleted_at timestamp
}

Ref: books.seller_id > sellers.id [delete: set null]

Table carts {
  id bigint [pk, increment]
  user_id bigint [not null]
  book_id bigint [not null]
  quantity int [not null, default: 1]
  created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
  updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
  deleted_at timestamp
}

Ref: carts.user_id > users.id [delete: cascade]
Ref: carts.book_id > books.id [delete: cascade]

Table orders {
  id bigint [pk, increment]
  user_id bigint [not null]
  status varchar(20) [not null, default: 'CREATED']
  total_amount decimal(10,2) [not null]
  created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
  updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
}

Ref: orders.user_id > users.id [delete: restrict]

Table order_items {
  id bigint [pk, increment]
  order_id bigint [not null]
  book_id bigint [not null]
  quantity int [not null]
  price decimal(10,2) [not null]
  status varchar(20) [not null, default: 'CREATED']
  created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
}

Ref: order_items.order_id > orders.id [delete: cascade]
Ref: order_items.book_id > books.id [delete: restrict]

Table reviews {
  id bigint [pk, increment]
  user_id bigint [not null]
  book_id bigint [not null]
  rating int [not null]
  comment text [not null]
  created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
  updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
  deleted_at timestamp
}

Ref: reviews.user_id > users.id [delete: cascade]
Ref: reviews.book_id > books.id [delete: cascade]

Table comments {
  id bigint [pk, increment]
  user_id bigint [not null]
  review_id bigint [not null]
  content text [not null]
  created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
  updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
  deleted_at timestamp
}

Ref: comments.user_id > users.id [delete: cascade]
Ref: comments.review_id > reviews.id [delete: cascade]

Table likes {
  id bigint [pk, increment]
  user_id bigint [not null]
  target_type varchar(20) [not null]
  target_id bigint [not null]
  created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
}

Ref: likes.user_id > users.id [delete: cascade]

Table favorites {
  id bigint [pk, increment]
  user_id bigint [not null]
  book_id bigint [not null]
  created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
  deleted_at timestamp
}

Ref: favorites.user_id > users.id [delete: cascade]
Ref: favorites.book_id > books.id [delete: cascade]

Table book_views {
  id bigint [pk, increment]
  user_id bigint [not null]
  book_id bigint [not null]
  view_count int [not null, default: 1]
  created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
  updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
}

Ref: book_views.user_id > users.id [delete: cascade]
Ref: book_views.book_id > books.id [delete: cascade]

Table discounts {
  id bigint [pk, increment]
  book_id bigint [not null]
  discount_rate decimal(5,2) [not null]
  start_at timestamp [not null]
  end_at timestamp [not null]
  is_valid boolean [not null, default: true]
  created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
  updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
}

Ref: discounts.book_id > books.id [delete: cascade]

Table coupons {
  id bigint [pk, increment]
  discount_rate decimal(5,2) [not null]
  start_at timestamp [not null]
  end_at timestamp [not null]
  is_valid boolean [not null, default: true]
  created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
  updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
}

Table user_coupons {
  id bigint [pk, increment]
  user_id bigint [not null]
  coupon_id bigint [not null]
  is_used boolean [not null, default: false]
  used_at timestamp
  created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
}

Ref: user_coupons.user_id > users.id [delete: cascade]
Ref: user_coupons.coupon_id > coupons.id [delete: cascade]

Table settlements {
  id bigint [pk, increment]
  seller_id bigint [not null]
  total_sales decimal(12,2) [not null]
  commission decimal(12,2) [not null]
  final_payout decimal(12,2) [not null]
  period_start date [not null]
  period_end date [not null]
  settlement_date timestamp [not null]
  created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
  updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
}

Ref: settlements.seller_id > sellers.id [delete: restrict]

Table settlement_items {
  id bigint [pk, increment]
  settlement_id bigint [not null]
  order_item_id bigint [not null]
  item_amount decimal(10,2) [not null]
  created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
}

Ref: settlement_items.settlement_id > settlements.id [delete: cascade]
Ref: settlement_items.order_item_id > order_items.id [delete: restrict]

Table book_stats {
  id bigint [pk, increment]
  book_id bigint [not null]
  view_count int [not null, default: 0]
  purchase_count int [not null, default: 0]
  favorite_count int [not null, default: 0]
  favorite_cancel_count int [not null, default: 0]
  cart_delete_count int [not null, default: 0]
  favorite_then_purchase_count int [not null, default: 0]
  view_then_purchase_count int [not null, default: 0]
  created_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
  updated_at timestamp [not null, default: `CURRENT_TIMESTAMP`]
}

Ref: book_stats.book_id > books.id [delete: cascade]
