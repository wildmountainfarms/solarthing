void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  int raw = analogRead(A1);
//   float voltage = raw * 18.4f / 1024.0f;
//   Serial.println(voltage);
  Serial.println(raw);
  delay(200);
}
