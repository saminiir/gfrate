CREATE OR REPLACE PROCEDURE SP_ADD_SERVER_TOKEN
(
P_CONSUMER_KEY      IN        VARCHAR2,
P_USER_ID           IN        NUMBER,
P_NAME              IN        VARCHAR2,
P_TOKEN_TYPE        IN        VARCHAR2,
P_TOKEN             IN        VARCHAR2,
P_TOKEN_SECRET      IN        VARCHAR2,
P_TOKEN_INTERVAL_IN_SEC         IN        NUMBER,
P_RESULT            OUT       NUMBER
)
AS
 
 -- Add a request token we obtained from a server.
V_OCR_ID                                NUMBER;
V_TOKEN_TTL                             DATE;

V_EXC_INVALID_CONSUMER_KEY              EXCEPTION;
BEGIN
P_RESULT := 0;

      BEGIN
        SELECT OCR_ID INTO V_OCR_ID FROM OAUTH_CONSUMER_REGISTRY
        WHERE OCR_CONSUMER_KEY = P_CONSUMER_KEY AND OCR_USA_ID_REF = P_USER_ID;
      EXCEPTION
      WHEN NO_DATA_FOUND THEN
          RAISE V_EXC_INVALID_CONSUMER_KEY;
      END;
     
      DELETE FROM OAUTH_CONSUMER_TOKEN
      WHERE OCT_OCR_ID_REF = V_OCR_ID
      AND OCT_USA_ID_REF = P_USER_ID
      AND UPPER(OCT_TOKEN_TYPE) = UPPER(P_TOKEN_TYPE)
      AND OCT_NAME       = P_NAME;
      
      IF P_TOKEN_INTERVAL_IN_SEC IS NOT NULL THEN
         V_TOKEN_TTL := SYSDATE + (P_TOKEN_INTERVAL_IN_SEC/(24*60*60));
      ELSE
         V_TOKEN_TTL := TO_DATE('9999.12.31', 'yyyy.mm.dd');
      END IF;
      
      INSERT INTO OAUTH_CONSUMER_TOKEN
      (OCT_ID, OCT_OCR_ID_REF,OCT_USA_ID_REF, OCT_NAME, OCT_TOKEN, OCT_TOKEN_SECRET, OCT_TOKEN_TYPE, OCT_TIMESTAMP, OCT_TOKEN_TTL)
      VALUES
      (SEQ_OCT_ID.NEXTVAL, V_OCR_ID, P_USER_ID, P_NAME, P_TOKEN, P_TOKEN_SECRET, UPPER(P_TOKEN_TYPE), SYSDATE, V_TOKEN_TTL);
       
EXCEPTION
WHEN V_EXC_INVALID_CONSUMER_KEY THEN
P_RESULT := 2; -- INVALID_CONSUMER_KEY
WHEN OTHERS THEN
-- CALL THE FUNCTION TO LOG ERRORS
ROLLBACK;
P_RESULT := 1; -- ERROR
END;
/
