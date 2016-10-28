package com.auth0.jwtdecodejava;

import com.auth0.jwtdecodejava.enums.Algorithm;
import com.auth0.jwtdecodejava.exceptions.JWTException;
import com.auth0.jwtdecodejava.impl.BaseClaim;
import com.auth0.jwtdecodejava.interfaces.Claim;
import com.auth0.jwtdecodejava.interfaces.JWT;
import com.sun.istack.internal.Nullable;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;

import static com.auth0.jwtdecodejava.Utils.base64Encode;
import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

public class JWTDecoderTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void getSubject() throws Exception {
        JWT jwt = JWTDecoder.decode("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ");
        assertThat(jwt.getSubject(), is(notNullValue()));
        assertThat(jwt.getSubject(), is("1234567890"));
    }

    // Exceptions
    @Test
    public void shouldThrowIfLessThan3Parts() throws Exception {
        exception.expect(JWTException.class);
        exception.expectMessage("The token was expected to have 3 parts, but got 2.");
        JWTDecoder.decode("two.parts");
    }

    @Test
    public void shouldThrowIfMoreThan3Parts() throws Exception {
        exception.expect(JWTException.class);
        exception.expectMessage("The token was expected to have 3 parts, but got 4.");
        JWTDecoder.decode("this.has.four.parts");
    }

    @Test
    public void shouldThrowIfPayloadHasInvalidJSONFormat() throws Exception {
        String validJson = "{}";
        String invalidJson = "{}}{";
        exception.expect(JWTException.class);
        exception.expectMessage(String.format("The string '%s' doesn't have a valid JSON format.", invalidJson));
        customJWT(validJson, invalidJson, "signature");
    }

    @Test
    public void shouldThrowIfHeaderHasInvalidJSONFormat() throws Exception {
        String validJson = "{}";
        String invalidJson = "{}}{";
        exception.expect(JWTException.class);
        exception.expectMessage(String.format("The string '%s' doesn't have a valid JSON format.", invalidJson));
        customJWT(invalidJson, validJson, "signature");
    }

    // toString
    @Test
    public void shouldGetStringToken() throws Exception {
        JWT jwt = JWTDecoder.decode("eyJhbGciOiJIUzI1NiJ9.e30.XmNK3GpH3Ys_7wsYBfq4C3M6goz71I7dTgUkuIa5lyQ");
        assertThat(jwt, is(notNullValue()));
        assertThat(jwt.toString(), is(notNullValue()));
        assertThat(jwt.toString(), is("eyJhbGciOiJIUzI1NiJ9.e30.XmNK3GpH3Ys_7wsYBfq4C3M6goz71I7dTgUkuIa5lyQ"));
    }

    // Parts

    @Test
    public void shouldGetHeader() throws Exception {
        JWT jwt = JWTDecoder.decode("eyJhbGciOiJIUzI1NiJ9.e30.XmNK3GpH3Ys_7wsYBfq4C3M6goz71I7dTgUkuIa5lyQ");
        assertThat(jwt, is(notNullValue()));
        assertThat(jwt.getAlgorithm(), is(Algorithm.HS256));
    }

    @Test
    public void shouldGetSignature() throws Exception {
        JWT jwt = JWTDecoder.decode("eyJhbGciOiJIUzI1NiJ9.e30.XmNK3GpH3Ys_7wsYBfq4C3M6goz71I7dTgUkuIa5lyQ");
        assertThat(jwt, is(notNullValue()));
        assertThat(jwt.getSignature(), is("XmNK3GpH3Ys_7wsYBfq4C3M6goz71I7dTgUkuIa5lyQ"));
    }

    // Public Claims

    @Test
    public void shouldGetIssuer() throws Exception {
        JWT jwt = JWTDecoder.decode("eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJKb2huIERvZSJ9.SgXosfRR_IwCgHq5lF3tlM-JHtpucWCRSaVuoHTbWbQ");
        assertThat(jwt, is(notNullValue()));
        assertThat(jwt.getIssuer(), is("John Doe"));
    }

    @Test
    public void shouldGetSubject() throws Exception {
        JWT jwt = JWTDecoder.decode("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJUb2szbnMifQ.RudAxkslimoOY3BLl2Ghny3BrUKu9I1ZrXzCZGDJtNs");
        assertThat(jwt, is(notNullValue()));
        assertThat(jwt.getSubject(), is("Tok3ns"));
    }

    @Test
    public void shouldGetArrayAudience() throws Exception {
        JWT jwt = JWTDecoder.decode("eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOlsiSG9wZSIsIlRyYXZpcyIsIlNvbG9tb24iXX0.Tm4W8WnfPjlmHSmKFakdij0on2rWPETpoM7Sh0u6-S4");
        assertThat(jwt, is(notNullValue()));
        assertThat(jwt.getAudience(), is(arrayWithSize(3)));
        assertThat(jwt.getAudience(), is(arrayContaining("Hope", "Travis", "Solomon")));
    }

    @Test
    public void shouldGetStringAudience() throws Exception {
        JWT jwt = JWTDecoder.decode("eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJKYWNrIFJleWVzIn0.a4I9BBhPt1OB1GW67g2P1bEHgi6zgOjGUL4LvhE9Dgc");
        assertThat(jwt, is(notNullValue()));
        assertThat(jwt.getAudience(), is(arrayWithSize(1)));
        assertThat(jwt.getAudience(), is(arrayContaining("Jack Reyes")));
    }

    @Test
    public void shouldGetExpirationTime() throws Exception {
        JWT jwt = JWTDecoder.decode("eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE0NzY3MjcwODZ9.L9dcPHEDQew2u9MkDCORFkfDGcSOsgoPqNY-LUMLEHg");
        assertThat(jwt, is(notNullValue()));
        assertThat(jwt.getExpiresAt(), is(instanceOf(Date.class)));
        long ms = 1476727086L * 1000;
        Date expectedDate = new Date(ms);
        assertThat(jwt.getExpiresAt(), is(notNullValue()));
        assertThat(jwt.getExpiresAt(), is(equalTo(expectedDate)));
    }

    @Test
    public void shouldGetNotBefore() throws Exception {
        JWT jwt = JWTDecoder.decode("eyJhbGciOiJIUzI1NiJ9.eyJuYmYiOjE0NzY3MjcwODZ9.tkpD3iCPQPVqjnjpDVp2bJMBAgpVCG9ZjlBuMitass0");
        assertThat(jwt, is(notNullValue()));
        assertThat(jwt.getNotBefore(), is(instanceOf(Date.class)));
        long ms = 1476727086L * 1000;
        Date expectedDate = new Date(ms);
        assertThat(jwt.getNotBefore(), is(notNullValue()));
        assertThat(jwt.getNotBefore(), is(equalTo(expectedDate)));
    }

    @Test
    public void shouldGetIssuedAt() throws Exception {
        JWT jwt = JWTDecoder.decode("eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE0NzY3MjcwODZ9.KPjGoW665E8V5_27Jugab8qSTxLk2cgquhPCBfAP0_w");
        assertThat(jwt, is(notNullValue()));
        assertThat(jwt.getIssuedAt(), is(instanceOf(Date.class)));
        long ms = 1476727086L * 1000;
        Date expectedDate = new Date(ms);
        assertThat(jwt.getIssuedAt(), is(notNullValue()));
        assertThat(jwt.getIssuedAt(), is(equalTo(expectedDate)));
    }

    @Test
    public void shouldGetId() throws Exception {
        JWT jwt = JWTDecoder.decode("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMjM0NTY3ODkwIn0.m3zgEfVUFOd-CvL3xG5BuOWLzb0zMQZCqiVNQQOPOvA");
        assertThat(jwt, is(notNullValue()));
        assertThat(jwt.getId(), is("1234567890"));
    }

    @Test
    public void shouldBeExpired() throws Exception {
        long pastSeconds = System.currentTimeMillis() / 1000;
        long futureSeconds = (System.currentTimeMillis() + 10000) / 1000;

        JWT issuedAndExpiresInTheFuture = customTimeJWT(futureSeconds, futureSeconds);
        assertTrue(issuedAndExpiresInTheFuture.isExpired());
        JWT issuedInTheFuture = customTimeJWT(futureSeconds, null);
        assertTrue(issuedInTheFuture.isExpired());

        JWT issuedAndExpiresInThePast = customTimeJWT(pastSeconds, pastSeconds);
        assertTrue(issuedAndExpiresInThePast.isExpired());
        JWT expiresInThePast = customTimeJWT(null, pastSeconds);
        assertTrue(expiresInThePast.isExpired());

        JWT issuedInTheFutureExpiresInThePast = customTimeJWT(futureSeconds, pastSeconds);
        assertTrue(issuedInTheFutureExpiresInThePast.isExpired());
    }

    @Test
    public void shouldNotBeExpired() throws Exception {
        long pastSeconds = System.currentTimeMillis() / 1000;
        long futureSeconds = (System.currentTimeMillis() + 10000) / 1000;

        JWT missingDates = customTimeJWT(null, null);
        assertFalse(missingDates.isExpired());

        JWT issuedInThePastExpiresInTheFuture = customTimeJWT(pastSeconds, futureSeconds);
        assertFalse(issuedInThePastExpiresInTheFuture.isExpired());

        JWT issuedInThePast = customTimeJWT(pastSeconds, null);
        assertFalse(issuedInThePast.isExpired());

        JWT expiresInTheFuture = customTimeJWT(null, futureSeconds);
        assertFalse(expiresInTheFuture.isExpired());
    }

    //Private Claims

    @Test
    public void shouldGetMissingClaimIfClaimDoesNotExist() throws Exception {
        JWT jwt = JWTDecoder.decode("eyJhbGciOiJIUzI1NiJ9.e30.K17vlwhE8FCMShdl1_65jEYqsQqBOVMPUU9IgG-QlTM");
        assertThat(jwt, is(notNullValue()));
        assertThat(jwt.getClaim("notExisting"), is(notNullValue()));
        assertThat(jwt.getClaim("notExisting"), is(instanceOf(BaseClaim.class)));
    }

    @Test
    public void shouldGetValidClaim() throws Exception {
        JWT jwt = JWTDecoder.decode("eyJhbGciOiJIUzI1NiJ9.eyJvYmplY3QiOnsibmFtZSI6ImpvaG4ifX0.lrU1gZlOdlmTTeZwq0VI-pZx2iV46UWYd5-lCjy6-c4");
        assertThat(jwt, is(notNullValue()));
        assertThat(jwt.getClaim("object"), is(notNullValue()));
        assertThat(jwt.getClaim("object"), is(instanceOf(Claim.class)));
    }

    @Test
    public void shouldGetNullClaimIfClaimValueIsNull() throws Exception {
        JWT jwt = JWTDecoder.decode("eyJhbGciOiJIUzI1NiJ9.eyJvYmplY3QiOnt9fQ.d3nUeeL_69QsrHL0ZWij612LHEQxD8EZg1rNoY3a4aI");
        assertThat(jwt, is(notNullValue()));
        assertThat(jwt.getClaim("object"), is(notNullValue()));
        assertThat(jwt.getClaim("object").isNull(), is(true));
    }

    //Helper Methods

    private JWT customTimeJWT(@Nullable Long iat, @Nullable Long exp) {
        String header = base64Encode("{}");
        StringBuilder bodyBuilder = new StringBuilder("{");
        if (iat != null) {
            bodyBuilder.append("\"iat\":\"").append(iat.longValue()).append("\"");
        }
        if (exp != null) {
            if (iat != null) {
                bodyBuilder.append(",");
            }
            bodyBuilder.append("\"exp\":\"").append(exp.longValue()).append("\"");
        }
        bodyBuilder.append("}");
        String body = base64Encode(bodyBuilder.toString());
        String signature = "sign";
        return JWTDecoder.decode(String.format("%s.%s.%s", header, body, signature));
    }

    private JWT customJWT(String jsonHeader, String jsonPayload, String signature) {
        String header = base64Encode(jsonHeader);
        String body = base64Encode(jsonPayload);
        return JWTDecoder.decode(String.format("%s.%s.%s", header, body, signature));
    }


}