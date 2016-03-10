/*
 * Copyright (C) 2015 hcadavid
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.eci.pdsw.webappsintro.jdbc.example.basic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class JDBCExample {
    
    public static void main(String args[]){
        try {
            String url="jdbc:mysql://desarrollo.is.escuelaing.edu.co:3306/bdprueba";
            String driver="com.mysql.jdbc.Driver";
            String user="bdprueba";
            String pwd="bdprueba";
                        
            Class.forName(driver);
            Connection con=DriverManager.getConnection(url,user,pwd);
            con.setAutoCommit(false);
                 
            
            System.out.println("Valor total pedido 1:"+valorTotalPedido(con, 1));
            
            List<String> prodsPedido=nombresProductosPedido(con, 1);
            
            
            System.out.println("Productos del pedido 1:");
            System.out.println("-----------------------");
            for (String nomprod:prodsPedido){
                System.out.println(nomprod);
            }
            System.out.println("-----------------------");
            
            
            int suCodigoECI=2105534;
            registrarNuevoProducto(con, suCodigoECI, "Carlos Sanchez", 99999999);            
            con.commit();
            
            cambiarNombreProducto(con, suCodigoECI, "EL NUEVO NOMBRE");
            con.commit();
            
            
            con.close();
                                   
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCExample.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    /**
     * Agregar un nuevo producto con los parámetros dados
     * @param con la conexión JDBC
     * @param codigo
     * @param nombre
     * @param precio
     * @throws SQLException 
     */
    public static void registrarNuevoProducto(Connection con, int codigo, String nombre,int precio) throws SQLException{
        //Crear preparedStatement
        PreparedStatement statement;
        String consulta="INSERT INTO ORD_PRODUCTOS (codigo,nombre,precio) values (?,?,?)" ;
        statement=con.prepareStatement(consulta);
        //Asignar parámetros
        statement.setInt(0,codigo);
        statement.setString(1, nombre);
        statement.setInt(2,precio);
        //usar 'execute'
        statement.execute();
    }
    
    /**
     * Consultar los nombres de los productos asociados a un pedido
     * @param con la conexión JDBC
     * @param codigoPedido el código del pedido
     * @return 
     */
    public static List<String> nombresProductosPedido(Connection con, int codigoPedido) throws SQLException{
        List<String> np=new LinkedList<>();
        
        //Crear prepared statement
        PreparedStatement statement;
        String consulta="SELECT nombre FROM ORD_PRODUCTOS,ORD_PEDIDOS,ORD_DETALLES_PEDIDO WHERE ORD_PEDIDOS.codigo=? and ORD_PEDIDOS.codigo=ORD_DETALLES_PEDIDO.pedido_fk and ORD_DETALLES_PEDIDO.producto_fk=ORD_PRODUCTOS.codigo";
        statement=con.prepareStatement(consulta);
        //asignar parámetros
        statement.setInt(0,codigoPedido);
        //usar executeQuery
        ResultSet rs=statement.executeQuery();
        //Sacar resultados del ResultSet
        //Llenar la lista y retornarla
        while(rs.next()){
            np.add(rs.getString(0));
        }
        return np;
    }

    
     /**
     * Calcular el costo total de un pedido
     * @param con
     * @param codigoPedido código del pedido cuyo total se calculará
     * @return el costo total del pedido (suma de: cantidades*precios)
     */
    public static int valorTotalPedido(Connection con, int codigoPedido) throws SQLException{
        int ans = 0;
        //Crear prepared statement
        PreparedStatement statement;
        String consulta="SELECT SUM(precio) FROM ORD_PRODUCTOS,ORD_PEDIDOS,ORD_DETALLES_PEDIDO WHERE ORD_PEDIDOS.codigo=? and ORD_PEDIDOS.codigo=ORD_DETALLES_PEDIDO.pedido_fk and ORD_DETALLES_PEDIDO.producto_fk=ORD_PRODUCTOS.codigo";
        statement=con.prepareStatement(consulta);
        //asignar parámetros
        statement.setInt(0,codigoPedido);
        //usar executeQuery
        ResultSet rs=statement.executeQuery();
        //Sacar resultado del ResultSet
        ans=rs.getInt(0);
        return ans;
    }
    
    
    /**
     * Cambiar el nombre de un producto
     * @param con
     * @param codigoProducto codigo del producto cuyo nombre se cambiará
     * @param nuevoNombre el nuevo nombre a ser asignado
     */
    public static void cambiarNombreProducto(Connection con, int codigoProducto, String nuevoNombre) throws SQLException{
        //Crear prepared statement
        PreparedStatement statement;
        String consulta="UPDATE ORD_PRODUCTOS SET nombre=? WHERE codigo=?";
        statement=con.prepareStatement(consulta);
        //asignar parámetros
        statement.setInt(0,codigoProducto);        
        statement.setString(1,nuevoNombre);        
        //usar executeUpdate
        int rs=statement.executeUpdate();
        //verificar que se haya actualizado exactamente un registro
        System.out.print("Verificando: ");
        System.out.println(rs==1);
    }
}
