CREATE TABLE users (
    id serial primary key not null,
    name varchar(255) not null,
    email varchar(255) not null,
    password varchar(255) not null,
    pin varchar(255) not null,
    email_verified boolean default false,
    created_at timestamp default now(),
    updated_at timestamp default now(),
    deleted_at timestamp
);

CREATE TYPE login_providers AS ENUM (
    'LOCAL', 'GOOGLE', 'FACEBOOK', 'TWITTER', 'GITHUB'
);

CREATE TABLE user_providers (
    id serial primary key not null,
    user_id integer references users(id) on delete cascade,
    provider login_providers not null,
    provider_user_id varchar(255) not null,
    provider_access_token varchar(255),
    provider_refresh_token varchar(255),
    provider_expires_at timestamp,
    created_at timestamp default now(),
    updated_at timestamp default now(),
    deleted_at timestamp
);

CREATE TABLE roles (
    id serial primary key not null,
    name varchar(255) not null,
    description text,
    created_at timestamp default now(),
    updated_at timestamp default now(),
    deleted_at timestamp
);

CREATE TABLE user_roles (
    id serial primary key not null,
    user_id integer references users(id) on delete cascade,
    role_id integer references roles(id) on delete cascade,
    created_at timestamp default now(),
    updated_at timestamp default now(),
    deleted_at timestamp
);

CREATE TYPE token_type AS ENUM (
    'EMAIL_VERIFICATION',
    'PASSWORD_RESET',
    'ACCESS',
    'REFRESH'
);

CREATE TABLE user_tokens (
    id serial primary key not null,
    user_id integer references users(id) on delete cascade,
    token varchar(255) not null,
    token_type token_type not null,
    expires_at timestamp,
    created_at timestamp default now(),
    updated_at timestamp default now(),
    deleted_at timestamp
);

CREATE TABLE wallet (
    id serial primary key not null,
    user_id int references users(id) on delete no action ,
    budget numeric(15, 2) not null default 0,
    name varchar(255) not null,
    description text,
    is_active boolean default true,
    created_at timestamp default now(),
    updated_at timestamp default now(),
    deleted_at timestamp
);

CREATE TABLE pocket (
    id serial primary key not null ,
    wallet_id int references wallet(id) on delete no action ,
    budget numeric(15, 2) not null default 0,
    realized numeric(15, 2) not null default 0,
    image_url varchar(255) not null,
    name varchar(255) not null,
    description text,
    is_goal boolean default false,
    created_at timestamp default now(),
    updated_at timestamp default now(),
    deleted_at timestamp
);

CREATE TYPE transaction_type AS ENUM (
    'INCOME',
    'EXPENSE',
);

CREATE TABLE transactions (
    id serial primary key not null,
    user_id int references users(id) on delete no action ,
    pocket_id int references pocket(id) on delete no action ,
    attachment_url varchar(255) not null,
    description text,
    amount numeric(15, 2) not null default 0,
    transaction_type transaction_type not null default 'EXPENSE',
    created_at timestamp default now(),
    updated_at timestamp default now(),
    deleted_at timestamp
);