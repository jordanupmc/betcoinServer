package bd;

public enum CryptoEnum {
    ETH("Ethereum"),
    BTC("Bitcoin"),
    ETC("EthereumClassic"),
    LTC("LiteCoin"),
    EOS("EOS"),
    BCH("BitcoinCash"),
    XRP("XRP"),
    ZEC("ZCash"),
    NEO("NEO"),
    Dash("Dash");

    private final String readable;

    CryptoEnum(String cryptoName) {
        this.readable = cryptoName;
    }

    public String readable(){
        return readable;
    }
}
