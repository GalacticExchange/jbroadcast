public class GexECDSA {


    /**
     * get public key from private
     */
    public PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        PrivateKey privateKey = pair.getPrivate();
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

//        ECNamedCurveParameterSpec parameterSpec = ECNamedCurveTable.getParameterSpec(ALGORITHM);
        ECNamedCurveParameterSpec parameterSpec = ECNamedCurveTable.getParameterSpec("secp192r1");

        ECDomainParameters ecDomainParameters =
                new ECDomainParameters(
                        parameterSpec.getCurve(),
                        parameterSpec.getG(),
                        parameterSpec.getN(), parameterSpec.getH(), parameterSpec.getSeed());

        byte[] privateKeyBytes = privateKey.getEncoded();

        ECPoint ecPoint = ecDomainParameters.getG().multiply(new BigInteger(privateKeyBytes));


        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(ecPoint.getEncoded());
        return keyFactory.generatePublic(pubKeySpec);

    }
}