/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Stock;

import java.awt.Image;
import java.awt.event.KeyEvent;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author angel
 */
public class Compras extends javax.swing.JFrame {

    /**
     * Creates new form Compras
     */
    public Compras() {
        initComponents();
        Connect();
        Vendedor();
        
        ImageIcon icRegresar = new ImageIcon(getClass() .getResource("/Imagenes/regresar1.png"));
        Icon icRegr = new ImageIcon(icRegresar.getImage().getScaledInstance(26,26, Image.SCALE_DEFAULT));
        btnCerrar.setIcon(icRegr);
    }
    
    static Connection con = null;
    PreparedStatement pst;
    PreparedStatement pst1;
    PreparedStatement pst2;
    DefaultTableModel df;
    ResultSet rs;
    
    
    public void Connect(){
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/manejoinventario", "root", "");
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Compras.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Compras.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    
    public void Vendedor(){
        
        try {
            pst = con.prepareStatement("select Distinct nombre from vendedor");
            rs = pst.executeQuery();
            
            cmbVendedor.removeAllItems();
            
            while(rs.next()){
                cmbVendedor.addItem(rs.getNString("nombre"));
            
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Compras.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    
    public void codigoBarras(){
        
        try {
            String codp = txtCodp.getText();
            
            pst = con.prepareStatement("select * from product where cod = ?");
            pst.setString(1, codp);
            rs = pst.executeQuery();
            
            if(rs.next() == false)
            {
                JOptionPane.showMessageDialog(this, "Código no encontrado");
                txtCodp.setText("");
            
            }
            else{
                
                String nombrep = rs.getString("pnombre");
                String precio = rs.getString("retail");
                
                txtNombrep.setText(nombrep.trim());
                txtPrecio.setText(precio.trim());
                txtCant.requestFocus();
                
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Compras.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    public void compras(){
        
        int precio = Integer.parseInt(txtPrecio.getText());
        int cant = Integer.parseInt(txtCant.getText());
        
        int total = precio * cant;
        
        df = (DefaultTableModel)jTable1.getModel();
        df.addRow(new Object[]{
            
            txtCodp.getText(),
            txtNombrep.getText(),
            txtPrecio.getText(),
            txtCant.getText(),
            total
        
        });
        
        int suma = 0;
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            suma = suma + Integer.parseInt(jTable1.getValueAt(i, 4).toString());
            
        }
        
        txtCostot.setText(String.valueOf(suma));
        txtCodp.setText("");
        txtNombrep.setText("");
        txtPrecio.setText("");
        txtCant.setText("");
        
    }
    
    public void agregar(){
        
        try {
            DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDateTime now = LocalDateTime.now();
            String fecha = dt.format(now);
            
            String vendedor = cmbVendedor.getSelectedItem().toString();
            String subtotal = txtCostot.getText();
            String pago = txtPago.getText();
            String balance = txtBalance.getText();
            int lastid = 0;
            
            String query1 = "insert into compras(fecha, vendedor, subtotal, pago, balance) values(?,?,?,?,?)";
            pst = con.prepareStatement(query1,Statement.RETURN_GENERATED_KEYS);
            
            pst.setString(1, fecha);
            pst.setString(2, vendedor);
            pst.setString(3, subtotal);
            pst.setString(4, pago);
            pst.setString(5, balance);
            pst.executeUpdate();
            rs = pst.getGeneratedKeys();
            
            if(rs.next()){
                
                lastid = rs .getInt(1);
                
            }
            
            String query2 = "insert into articuloadq(idcompra, idprod, rprecio, cantidad, total) values(?,?,?,?,?)";
            pst1 = con.prepareStatement(query2);
            
            String idprod;
            String precio;
            String cant;
            int total = 0;
            
            for (int i = 0; i < jTable1.getRowCount(); i++) {
                
                idprod = (String)jTable1.getValueAt(i, 0);
                precio = (String)jTable1.getValueAt(i, 2);
                cant = (String)jTable1.getValueAt(i, 3);
                total    = (int)jTable1.getValueAt(i, 4);
                
                pst1.setInt(1, lastid);
                pst1.setString(2, idprod);
                pst1.setString(3, precio);
                pst1.setString(4, cant);
                pst1.setInt(5, total);
                pst1.executeUpdate();
                
            }
            
            String query3 = "update product set cantidad = cantidad + ? where cod = ?";
            pst2 = con.prepareStatement(query3);
            
            for (int i = 0; i < jTable1.getRowCount(); i++) {
               
                idprod = (String)jTable1.getValueAt(i, 0);
                cant = (String)jTable1.getValueAt(i, 3);
                
                pst2.setString(1, cant);
                pst2.setString(2, idprod);
                pst2.executeUpdate();
                
            }
            
            JOptionPane.showMessageDialog(this, "Compra Completada");
            
        } catch (SQLException ex) {
            Logger.getLogger(Compras.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtCodp = new javax.swing.JTextField();
        txtNombrep = new javax.swing.JTextField();
        txtPrecio = new javax.swing.JTextField();
        btnAg1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        txtCant = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtCostot = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtPago = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtBalance = new javax.swing.JTextField();
        btnAg2 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        cmbVendedor = new javax.swing.JComboBox<>();
        btnCerrar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("Compras");

        jPanel1.setBackground(new java.awt.Color(153, 153, 255));
        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Código del producto");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Nombre del Producto");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Precio");

        txtCodp.setBackground(new java.awt.Color(204, 204, 255));
        txtCodp.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtCodp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodpActionPerformed(evt);
            }
        });
        txtCodp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCodpKeyPressed(evt);
            }
        });

        txtNombrep.setBackground(new java.awt.Color(204, 204, 255));
        txtNombrep.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtNombrep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombrepActionPerformed(evt);
            }
        });

        txtPrecio.setBackground(new java.awt.Color(204, 204, 255));
        txtPrecio.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        btnAg1.setBackground(new java.awt.Color(102, 102, 255));
        btnAg1.setText("Agregar");
        btnAg1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAg1ActionPerformed(evt);
            }
        });

        jTable1.setBackground(new java.awt.Color(153, 153, 255));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo Producto", "Nombre Producto", "Precio", "Cantidad", "Total"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Cantidad");

        txtCant.setBackground(new java.awt.Color(204, 204, 255));
        txtCant.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtCant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCantActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Costo total:");

        txtCostot.setBackground(new java.awt.Color(204, 204, 255));
        txtCostot.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtCostot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCostotActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Pago:");

        txtPago.setBackground(new java.awt.Color(204, 204, 255));
        txtPago.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Diferencia:");

        txtBalance.setBackground(new java.awt.Color(204, 204, 255));
        txtBalance.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtBalance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBalanceActionPerformed(evt);
            }
        });

        btnAg2.setBackground(new java.awt.Color(102, 102, 255));
        btnAg2.setText("Agregar pago");
        btnAg2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAg2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 796, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(366, 366, 366)
                        .addComponent(btnAg2, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(txtCostot, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                            .addContainerGap())
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(txtPago)
                                .addComponent(txtBalance))
                            .addContainerGap()))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(45, 45, 45))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(42, 42, 42))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCodp, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(44, 44, 44)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNombrep, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(49, 49, 49)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(50, 50, 50)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtCant, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(47, 47, 47)
                        .addComponent(btnAg1, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel7))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel7))
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCodp, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombrep, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAg1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCant, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnAg2, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCostot, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtPago, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtBalance, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Vendedor");

        cmbVendedor.setBackground(new java.awt.Color(153, 153, 255));

        btnCerrar.setBackground(new java.awt.Color(102, 102, 255));
        btnCerrar.setActionCommand("");
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(cmbVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(189, 189, 189))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel6)
                    .addComponent(cmbVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(58, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtNombrepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombrepActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombrepActionPerformed

    private void txtCodpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodpActionPerformed

    private void txtCodpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodpKeyPressed
        // TODO add your handling code here:
        
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            
            codigoBarras();
            
        }
    }//GEN-LAST:event_txtCodpKeyPressed

    private void btnAg1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAg1ActionPerformed
        // TODO add your handling code here:
        
        compras();
        
    }//GEN-LAST:event_btnAg1ActionPerformed

    private void txtCostotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCostotActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCostotActionPerformed

    private void txtCantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCantActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCantActionPerformed

    private void btnAg2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAg2ActionPerformed
        // TODO add your handling code here:
        
        int pago = Integer.parseInt(txtPago.getText());
        int subtotal = Integer.parseInt(txtCostot.getText());
        int balance = pago - subtotal;
        
        txtBalance.setText(String.valueOf(balance));
        
        
        agregar();
        
    }//GEN-LAST:event_btnAg2ActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        // TODO add your handling code here:
        
        this.setVisible(false);
        
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void txtBalanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBalanceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBalanceActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Compras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Compras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Compras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Compras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Compras().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAg1;
    private javax.swing.JButton btnAg2;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JComboBox<String> cmbVendedor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtBalance;
    private javax.swing.JTextField txtCant;
    private javax.swing.JTextField txtCodp;
    private javax.swing.JTextField txtCostot;
    private javax.swing.JTextField txtNombrep;
    private javax.swing.JTextField txtPago;
    private javax.swing.JTextField txtPrecio;
    // End of variables declaration//GEN-END:variables
}
