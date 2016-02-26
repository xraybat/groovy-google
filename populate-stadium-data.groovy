// populate-stadium-data.groovy

//??package service
package beans //??

// postgresql
//@Grab(group='postgresql', module='postgresql', version='8.4-701.jdbc4')
//@Grab(group='postgresql', module='postgresql', version='9.1-901.jdbc4')

// org.postgresql
//@Grab(group='postgresql', module='postgresql', version='9.4-1205.jdbc42')     // JDK 1.8
//@Grab(group='org.postgresql', module='postgresql', version='9.4-1205-jdbc41') // JDK 1.7

// mysql
//@Grab(group='mysql', module='mysql-connector-java', version='5.1.37')

@GrabConfig(systemClassLoader=true)
@Grapes([
  @Grab(group='mysql', module='mysql-connector-java', version='5.1.37')
  , @Grab(group='org.postgresql', module='postgresql', version='9.4-1205-jdbc41')
])

import groovy.sql.Sql

/*Sql dbPostgres = Sql.newInstance(
  'jdbc:postgres://ec2-54-83-202-64.compute-1.amazonaws.com:5432/d1prih5tn29hgl',
  'vjizkxnwgscyzm',
  '-Zi9dkF7xeJPw-PuM_uCm1Kkr0',
  'org.postgresql.Driver'
)*/

// move credentials to env variable and Config class??
def credentialsFile = new File('../xml/credentials.groovy')	// inbuilt config files and parsing of
def configSlurper = new ConfigSlurper()
def credentials = configSlurper.parse(credentialsFile.toURL())

Sql dbMySql = Sql.newInstance(
  'jdbc:mysql://localhost:3306/baseball',
  credentials.database.user,
  credentials.database.password,
  'com.mysql.jdbc.Driver'
)

dbMySql.execute 'drop table if exists stadium;'
dbMySql.execute '''
 create table stadium(
   id int not null auto_increment,
   name varchar(200) not null,
   city varchar(200) not null,
   state char(2) not null,
   team char(3) not null,
   latitude double,
   longitude double,
   primary key(id)
 );
'''

def stadiums = []
stadiums << new Stadium(name:'Angel Stadium',  city:'Anaheim',    state:'CA', team:'ana')
stadiums << new Stadium(name:'Chase Field',    city:'Phoenix',    state:'AZ', team:'ari')
stadiums << new Stadium(name:'Rogers Centre',  city:'Toronto',    state:'ON', team:'tor')
stadiums << new Stadium(name:'Nationals Park', city:'Washington', state:'DC', team:'was')
//println stadiums

def geo = new Geocoder()

stadiums.each { s ->
  geo.fillInLatLng s
  dbMySql.execute """
    insert into stadium(name, city, state, team, latitude, longitude)
    values(${s.name},
           ${s.city},
           ${s.state},
           ${s.team},
           ${s.latitude},
           ${s.longitude}
    );
  """
}

assert dbMySql.rows('select * from stadium').size() == stadiums.size()

dbMySql.eachRow('select latitude, longitude from stadium') { row ->
  assert row.latitude > 25 && row.latitude < 48
  assert row.longitude > -123 && row.longitude < -71
}
