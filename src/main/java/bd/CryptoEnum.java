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
    DASH("Dash");

    private final String readable;

    CryptoEnum(String cryptoName) {
        this.readable = cryptoName;
    }

    public String readable(){
        return readable;
    }

    public static boolean contains(String test) {

        for (CryptoEnum c : CryptoEnum.values()) {
            if (c.name().equals(test)) {
                return true;
            }
        }
        return false;
    }
}
