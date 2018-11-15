CREATE EXTENSION pgcrypto;

CREATE TABLE Users (
  idUser SERIAL PRIMARY KEY,
  login varchar(100) UNIQUE NOT NULL ,
  password text NOT NULL ,
  email varchar NOT NULL ,
  last_name varchar(100) NOT NULL ,
  first_name varchar(100) NOT NULL ,
  solde integer DEFAULT 500,
  isLock boolean DEFAULT false,
  birthday date,
  country varchar(80)
);

CREATE TYPE crypto_currency AS ENUM ('Bitcoin', 'Ethereum', 'XRP', 'EthereumClassic', 'LiteCoin', 'EOS', 'BitcoinCash', 'ZCash', 'NEO', 'Dash');

CREATE TABLE BetPool (
  idBetPool SERIAL PRIMARY KEY,
  name varchar NOT NULL,
  openingBet timestamp NOT NULL ,
  closingBet timestamp,
  resultBet timestamp,
  cryptoCurrency crypto_currency NOT NULL ,
  openingprice numeric,
  poolType boolean NOT NULL
);

CREATE OR REPLACE FUNCTION insert_bet_days()
  RETURNS trigger AS
$func$
BEGIN
  IF NEW.openingBet IS NULL
  then
    NEW.openingBet := date_trunc('hour', now()) AT TIME ZONE  'Europe/Paris';
  end if;
  IF NEW.closingBet IS NULL
  then
      NEW.closingBet := NEW.openingBet + interval '1 hour';
  END IF;
  IF NEW.resultBet IS NULL
  then
      NEW.resultBet := NEW.closingBet + interval '1 hour';
  END IF;
  IF NEW.name IS NULL
  then
      NEW.name :=  CAST( NEW.cryptoCurrency  AS TEXT) || '_' || CAST(NEW.openingBet AS TEXT );
  end if;
RETURN NEW;
END;
$func$ LANGUAGE plpgsql;

CREATE TRIGGER trig_insert_bet_days
BEFORE INSERT
ON BetPool
for each row
  WHEN (NEW.closingBet IS NULL OR NEW.resultBet IS NULL )
execute procedure insert_bet_days();
