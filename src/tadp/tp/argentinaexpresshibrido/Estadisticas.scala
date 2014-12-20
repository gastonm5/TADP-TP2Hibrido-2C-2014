package tadp.tp.argentinaexpresshibrido

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

case class Tupla (nombre:String, cantidad:String){
}

case class Tupla2 (nombre:String, tuplas:Set[Tupla]){
}

class Estadisticas extends prettyPrinter{

  def filtrarPorSucursalDestino(viajes: Set[Viaje], sucursalBuscada : Sucursal): Set[Viaje] ={
    viajes.filter(_.sucursalDestino == sucursalBuscada)
  }
  
  def filtrarPorSucursalOrigen(viajes: Set[Viaje], sucursalBuscada : Sucursal): Set[Viaje] ={
    viajes.filter(_.sucursalOrigen == sucursalBuscada)
  }
  
  def filtrarPorFecha(viajes: Set[Viaje], fechaBuscada : Date): Set[Viaje] ={
    viajes.filter(_.fecha == fechaBuscada)
  }

  //funcion identidad para el rango de fechas
  
  def sinFiltroFechas(viajes: Set[Viaje], fechaIni : Date, fechaFin : Date): Set[Viaje] ={
    viajes
  }
  
  def filtrarPorRangoFecha(viajes: Set[Viaje], fechaIni : Date, fechaFin : Date): Set[Viaje] ={
    viajes.filter(v => v.fecha.after(fechaIni) && v.fecha.before(fechaFin) )
  }
    
  def filtrarPorTipoDeEnvio(viajes: Set[Viaje], tipo : String): Set[Viaje] ={
    viajes.filter(_.tipoEnvio == tipo)
  }  

  def filtrarPorTipoDeTransporte(viajes: Set[Viaje], tipo : String): Set[Viaje] ={
    viajes.filter(v => nombreClase(v.transporte) == tipo)
  } 
  
  def tiempoPromedioDeViajes(viajes: Set[Viaje]) : String = {
   
    	 filtroMap(viajes, (_.tiempoHr))
  }  
  
  def costoPromedioDeViajes(viajes: Set[Viaje]) : String = {
 
    	 filtroMap(viajes, (_.costo))
  }

  def gananciaPromedioDeViajes(viajes: Set[Viaje]) : String = {
        filtroMap(viajes, (_.ganancia))
  }
  
  def filtroMap(viajes: Set[Viaje], f :(Viaje => Double)):String =
  {
    if(!viajes.isEmpty)
    	(viajes.toList.map(f).sum / viajes.size).toString
    else
    	0.toString
  }
  
  def cantidadDeViajes(viajes: Set[Viaje]) : String ={
    viajes.size.toString
  }
  
  def cantidadDeEnvios(viajes: Set[Viaje]) : String ={
        filtroSimpleMap(viajes, (_.pedidos.size))
  }
    
  def calcularFacturacionTotal(viajes: Set[Viaje]) : String ={
        filtroSimpleMap(viajes, (_.ganancia))
  }
  
  def filtroSimpleMap(viajes: Set[Viaje], f :(Viaje => Double)):String =
  {
    viajes.toList.map(f).sum.toString
  }
  
 
  //funcion identidad para las estadisticas
  def estadisticaIdentidad(viajes: Set[Viaje], comparacion: (Set[Viaje]) => String,filtroFecha: (Set[Viaje],Date,Date) => Set[Viaje],
      fechaIni : Date, fechaFin : Date): Set[Tupla]
  ={
    var viajesFiltrados: Set[Viaje] = filtroFecha(viajes,fechaIni,fechaFin)
    var datos:Set[Tupla]=Set()
    datos += new Tupla("sin filtro",comparacion(viajesFiltrados))
    datos
  }
  
  // Comparacion entre distintos tipos de Transporte
  def estadisticasPorTransporteSinFiltros(
      viajes: Set[Viaje], comparacion: (Set[Viaje]) => String,filtroFecha: (Set[Viaje],Date,Date) => Set[Viaje],
      fechaIni : Date, fechaFin : Date)
  :Set[Tupla] 
  ={
    
    val viajesCamion : Set[Viaje] = filtrarPorTipoDeTransporte(viajes, "Camion")
    val viajesFurgoneta : Set[Viaje] = filtrarPorTipoDeTransporte(viajes, "Furgoneta")
    val viajesAvion : Set[Viaje] = filtrarPorTipoDeTransporte(viajes, "Avion")
    
    val tuplaCamion: Tupla =new Tupla("camion", comparacion(viajesCamion))
    val tuplaFurgoneta: Tupla=new Tupla("furgoneta", comparacion(viajesFurgoneta))
    val tuplaAvion: Tupla=new Tupla("avion", comparacion(viajesAvion))
    var datos:Set[Tupla]=Set()
    datos += tuplaCamion 
    datos += tuplaFurgoneta 
    datos += tuplaAvion
    datos
   }
  
  def estadisticasPorTransporte(
      viajes: Set[Viaje], comparacion: (Set[Viaje]) => String,filtro: (Set[Viaje],Set[Viaje]=> String,(Set[Viaje],Date,Date) => Set[Viaje],Date,Date) => Set[Tupla],
      filtroFecha: (Set[Viaje],Date,Date) => Set[Viaje], fechaIni : Date, fechaFin : Date)
  :Set[Tupla] 
  ={
    
    val viajesCamion : Set[Viaje] = filtrarPorTipoDeTransporte(viajes, "Camion")
    val viajesFurgoneta : Set[Viaje] = filtrarPorTipoDeTransporte(viajes, "Furgoneta")
    val viajesAvion : Set[Viaje] = filtrarPorTipoDeTransporte(viajes, "Avion")
    
    val tuplaCamion: Tupla =new Tupla("camion", filtro(viajesCamion,comparacion,filtroFecha,fechaIni,fechaFin).toString)
    val tuplaFurgoneta: Tupla=new Tupla("furgoneta", filtro(viajesFurgoneta,comparacion,filtroFecha,fechaIni,fechaFin).toString)
    val tuplaAvion: Tupla=new Tupla("avion", filtro(viajesAvion,comparacion,filtroFecha,fechaIni,fechaFin).toString)
    var datos:Set[Tupla]=Set()
    datos += tuplaCamion 
    datos += tuplaFurgoneta 
    datos += tuplaAvion
    datos
   }
  
  //Ejemplos de distintos filtros posibles
  def enviosPorTipoTransporte(viajes: Set[Viaje]) :Set[Tupla]= {
    var datos:Set[Tupla]=Set()
    datos = estadisticasPorTransporte(viajes,cantidadDeEnvios,estadisticaIdentidad,sinFiltroFechas,new Date,new Date) //("ENVIOS POR TRANSPORTE","Cantidad")
    datos
  }
  
  def tiempoPromedioPorTransporte(viajes: Set[Viaje]) :Set[Tupla]= {
   var datos:Set[Tupla]=Set()
    datos= estadisticasPorTransporte(viajes,tiempoPromedioDeViajes,estadisticaIdentidad,sinFiltroFechas,new Date,new Date) //("TIEMPO PROMEDIO DE VIAJE","Tiempo(Hr)")
    datos
  }
  
   def costoPromedioPorTransporte(viajes: Set[Viaje]) :Set[Tupla]= {
    var datos:Set[Tupla]=Set()
    datos= estadisticasPorTransporte(viajes,costoPromedioDeViajes,estadisticaIdentidad,sinFiltroFechas,new Date,new Date) //("COSTO PROMEDIO DE VIAJE","Costo($)")
    datos
   }
   
  def gananciaPromedioPorTransporte(viajes: Set[Viaje]):Set[Tupla] = {
    var datos:Set[Tupla]=Set()
    datos= estadisticasPorTransporte(viajes,gananciaPromedioDeViajes,estadisticaIdentidad,sinFiltroFechas,new Date,new Date) //("GANANCIA PROMEDIO DE VIAJE","Ganancia($)")
    datos
  }
  
  def viajesPorTipoTransporte(viajes: Set[Viaje])  :Set[Tupla]= {
    var datos:Set[Tupla]=Set()
    datos= estadisticasPorTransporte(viajes,cantidadDeViajes,estadisticaIdentidad,sinFiltroFechas,new Date,new Date) //("VIAJES POR TRANSPORTE","Cantidad")
    datos
  }
  
  def estadisticasPorEnvioSinFiltros(
      viajes: Set[Viaje], comparacion: Set[Viaje] => String,filtroFecha: (Set[Viaje],Date,Date) => Set[Viaje],
      fechaIni : Date, fechaFin : Date) 
  ={
    val viajesNormal : Set[Viaje] = filtrarPorTipoDeEnvio(viajes, "Normal")
    val viajesUrgente : Set[Viaje] = filtrarPorTipoDeEnvio(viajes, "Urgente")
    val viajesRefrigeracion : Set[Viaje] = filtrarPorTipoDeEnvio(viajes, "Refrigeracion")
    val viajesFragil : Set[Viaje] = filtrarPorTipoDeEnvio(viajes, "Fragil")
    
    val tuplaNormal: Tupla=new Tupla("Normal", comparacion(viajesNormal))
    val tuplaUrgente: Tupla=new Tupla("Urgente", comparacion(viajesUrgente))
    val tuplaRefrigeracion: Tupla=new Tupla("Refrigeracion", comparacion(viajesRefrigeracion))
    val tuplaFragil: Tupla=new Tupla("Fragil", comparacion(viajesFragil))
    var datos:Set[Tupla]=Set()
    datos += tuplaNormal 
    datos += tuplaUrgente 
    datos += tuplaRefrigeracion 
    datos += tuplaFragil
    datos
  }
  
  def estadisticasPorEnvio(
      viajes: Set[Viaje], comparacion: Set[Viaje] => String,
      filtro: (Set[Viaje],Set[Viaje]=> String,(Set[Viaje],Date,Date) => Set[Viaje],Date,Date) => Set[Tupla],
      filtroFecha: (Set[Viaje],Date,Date) => Set[Viaje],
      fechaIni : Date, fechaFin : Date) :Set[Tupla]
  ={
    
    val viajesNormal : Set[Viaje] = filtrarPorTipoDeEnvio(viajes, "Normal")
    val viajesUrgente : Set[Viaje] = filtrarPorTipoDeEnvio(viajes, "Urgente")
    val viajesRefrigeracion : Set[Viaje] = filtrarPorTipoDeEnvio(viajes, "Refrigeracion")
    val viajesFragil : Set[Viaje] = filtrarPorTipoDeEnvio(viajes, "Fragil")
    
    val tuplaNormal: Tupla=new Tupla("camion", filtro(viajesNormal,comparacion,filtroFecha,fechaIni,fechaFin).toString)
    val tuplaUrgente: Tupla=new Tupla("furgoneta", filtro(viajesUrgente,comparacion,filtroFecha,fechaIni,fechaFin).toString)
    val tuplaRefrigeracion: Tupla=new Tupla("avion", filtro(viajesRefrigeracion,comparacion,filtroFecha,fechaIni,fechaFin).toString)
    val tuplaFragil: Tupla=new Tupla("avion", filtro(viajesFragil,comparacion,filtroFecha,fechaIni,fechaFin).toString)
    var datos:Set[Tupla]=Set()
    datos += tuplaNormal 
    datos += tuplaUrgente 
    datos += tuplaRefrigeracion 
    datos += tuplaFragil
    datos
    
  }
  
  // Comparacion entre distintas Sucursales
  // Comparacion entre distintas Sucursales
  def estadisticasPorSucursal(sucursales : Set[Sucursal], filtro: (Set[Viaje],Set[Viaje]=> String,(Set[Viaje],Date,Date) => Set[Viaje],Date,Date) => Set[Tupla],
      comparacion: Set[Viaje] => String, filtroFecha: (Set[Viaje],Date,Date) => Set[Viaje], fechaIni : Date, fechaFin : Date) :Set[Tupla2]
  ={
    var datos2:Set[Tupla2]=Set()
    var datos:Set[Tupla]=Set()
    sucursales.foreach({s =>
      var viajesSucursal = s.viajesRealizados 
      datos = datos ++ filtro(viajesSucursal,comparacion,filtroFecha,fechaIni,fechaFin)
      datos2 += new Tupla2(s.pais,datos)
      datos = Set()
    })
    datos2
  }
  
  def facturacionCompaniaPorSucursal(sucursales : Set[Sucursal]) = {
    estadisticasPorSucursal(sucursales,estadisticaIdentidad,calcularFacturacionTotal,sinFiltroFechas,new Date, new Date)
  }
 
  def facturacionTotalPorRangoFecha(viajes: Set[Viaje], fechaIni : Date, fechaFin : Date) ={
    val viajesFecha : Set[Viaje] = filtrarPorRangoFecha(viajes, fechaIni, fechaFin)
    
    println("FACTURACION POR RANGO DE FECHA")
    println("-----------------------")
    println("Fecha inicio: " + printDate(fechaIni))
    println("Fecha Fin   : " + printDate(fechaFin))
    println("Facturacion : " + calcularFacturacionTotal(viajesFecha))
    println("-----------------------\n")
  }
  
  /*def facturacionTotalPorFecha(viajes: Set[Viaje], fecha : Date) ={
    val viajesFecha : Set[Viaje] = filtrarPorFecha(viajes, fecha)
    
    println("FACTURACION POR FECHA")
    println("-----------------------")
    println("Fecha       : " + printDate(fecha))
    println("Facturacion : " + calcularFacturacionTotal(viajesFecha))
    println("-----------------------\n")
  }*/
  
}


trait prettyPrinter {
  def nombreClase(obj : Object): String = {
    var nombre : String = obj.getClass().toString()
    nombre = nombre.substring(nombre.lastIndexOf('.')+1)  
    nombre
  } 
  
  def printDate(fecha: Date): String = {
    fecha.getDate().toString + "/" + fecha.getMonth().toString + "/" + fecha.getYear().toString
  }
}