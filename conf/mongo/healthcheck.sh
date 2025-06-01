#!/bin/sh
set -e
SECRETS_FILE=/run/secrets/mongo_credentials
USER=$(grep '^admin_username=' "$SECRETS_FILE" | cut -d'=' -f2)
PASSWORD=$(grep '^admin_password=' "$SECRETS_FILE" | cut -d'=' -f2)

mongosh --host localhost:27017 --quiet \
  --tls --tlsCAFile /etc/ssl/mongo/ca.pem \
  -u "$USER" -p "$PASSWORD" --authenticationDatabase admin \
  --eval "
    // Фаза 1: Проверка базовой доступности
    const ping = db.runCommand({ ping: 1 });
    if (ping.ok !== 1) {
      print('Ping failed:', tojson(ping));
      quit(1);
    }
    // Фаза 2: Проверка реплики
    try {
      const state = rs.status().myState;
      if (state > 0) quit(0);
      print('Invalid replica state:', state);
      quit(1);
    } catch (e) {
      // Ожидаемая ошибка при отсутствии инициализации
      if (e.codeName === 'NotYetInitialized') {
        quit(0);
      }
      print('Replica check error:', e.message);
      quit(1);
    }
  "
# // Допустимые рабочие состояния
# if ([1, 2, 7].includes(state)) quit(0);  // PRIMARY, SECONDARY, ARBITER
#
# // Состояния инициализации (считаем прогрессом)
# if ([5, 6, 9, 10].includes(state)) {
#   print('Initialization progress:', state);
#   quit(0);  // Разрешаем как временно здоровое
# }