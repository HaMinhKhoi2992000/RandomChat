/*
 * Created by JFormDesigner on Thu Jan 13 12:11:32 ICT 2022
 */

package client.view.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import client.StartClient;
import shared.utils.ValidateUtil;

/**
 * @author Khoi Ha
 */
public class Login extends JFrame {
    public Login() {
        initComponents();
        setVisible(true);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                StartClient.clientSocketHandler.exit();
            }
        });

        txtNickname.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER){
                    btnLogin.doClick();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }

    private void btnLogin(ActionEvent e) {
        String nickname = txtNickname.getText();
        if (nickname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hãy nhập nickname bạn muốn", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else if (nickname.length() > 25) {
            JOptionPane.showMessageDialog(this, "Nickname của bạn quá dài", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else if (!ValidateUtil.isValidName(nickname, true).isEmpty()){
            JOptionPane.showMessageDialog(this, "Nickname không hợp lệ", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Chờ server kiểm tra login
            StartClient.clientSocketHandler.login(nickname);
        }
    }

   

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Khoi Ha
        PanelBody = new JPanel();
        lblLogin = new JLabel();
        panelContent = new JPanel();
        label1 = new JLabel();
        txtNickname = new JTextField();
        vSpacer1 = new JPanel(null);
        pgbLoading = new JProgressBar();
        btnLogin = new JButton();

        //======== this ========
        setTitle("Login");
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== PanelBody ========
        {
            PanelBody.setBackground(new Color(51, 51, 51));
            PanelBody.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder
            (0,0,0,0), "JF\u006frmDes\u0069gner \u0045valua\u0074ion",javax.swing.border.TitledBorder.CENTER,javax.swing.border
            .TitledBorder.BOTTOM,new java.awt.Font("D\u0069alog",java.awt.Font.BOLD,12),java.awt
            .Color.red),PanelBody. getBorder()));PanelBody. addPropertyChangeListener(new java.beans.PropertyChangeListener(){@Override public void
            propertyChange(java.beans.PropertyChangeEvent e){if("\u0062order".equals(e.getPropertyName()))throw new RuntimeException()
            ;}});
            PanelBody.setLayout(new BorderLayout());

            //---- lblLogin ----
            lblLogin.setText("\u0110\u0103ng nh\u1eadp");
            lblLogin.setHorizontalAlignment(SwingConstants.CENTER);
            lblLogin.setPreferredSize(new Dimension(58, 50));
            lblLogin.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            lblLogin.setForeground(Color.white);
            PanelBody.add(lblLogin, BorderLayout.NORTH);

            //======== panelContent ========
            {
                panelContent.setBackground(new Color(51, 51, 51));
                panelContent.setLayout(null);

                //---- label1 ----
                label1.setText("Nh\u1eadp nick name c\u1ee7a b\u1ea1n:");
                label1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                label1.setAlignmentX(0.5F);
                label1.setBorder(new EmptyBorder(0, 10, 0, 0));
                label1.setForeground(Color.white);
                panelContent.add(label1);
                label1.setBounds(0, 0, 398, label1.getPreferredSize().height);

                //---- txtNickname ----
                txtNickname.setBorder(new CompoundBorder(
                    new MatteBorder(0, 0, 2, 0, Color.white),
                    new EmptyBorder(0, 10, 0, 0)));
                txtNickname.setBackground(Color.darkGray);
                txtNickname.setForeground(Color.white);
                txtNickname.setFont(new Font("Tahoma", Font.PLAIN, 14));
                panelContent.add(txtNickname);
                txtNickname.setBounds(10, 35, 375, 25);

                //---- vSpacer1 ----
                vSpacer1.setBackground(new Color(51, 51, 51));
                panelContent.add(vSpacer1);
                vSpacer1.setBounds(5, 150, 395, 30);

                //---- pgbLoading ----
                pgbLoading.setVisible(false);
                panelContent.add(pgbLoading);
                pgbLoading.setBounds(135, 85, 130, pgbLoading.getPreferredSize().height);

                //---- btnLogin ----
                btnLogin.setText("\u0110\u0103ng nh\u1eadp");
                btnLogin.setBackground(Color.white);
                btnLogin.setForeground(Color.darkGray);
                btnLogin.setBorder(new MatteBorder(1, 1, 1, 1, Color.white));
                btnLogin.setBorderPainted(false);
                btnLogin.setFocusPainted(false);
                btnLogin.addActionListener(e -> btnLogin(e));
                panelContent.add(btnLogin);
                btnLogin.setBounds(135, 100, 130, 28);

                {
                    // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < panelContent.getComponentCount(); i++) {
                        Rectangle bounds = panelContent.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = panelContent.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    panelContent.setMinimumSize(preferredSize);
                    panelContent.setPreferredSize(preferredSize);
                }
            }
            PanelBody.add(panelContent, BorderLayout.CENTER);
        }
        contentPane.add(PanelBody, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    public void onFailed(String failedMsg) {
        setLoading(false, null);
        JOptionPane.showMessageDialog(this, failedMsg, "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
    }

    public void setLoading(boolean isLoading, String btnText) {
        pgbLoading.setVisible(isLoading);
        btnLogin.setEnabled(!isLoading);
        btnLogin.setText(isLoading ? btnText : "Tham gia");
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Khoi Ha
    private JPanel PanelBody;
    private JLabel lblLogin;
    private JPanel panelContent;
    private JLabel label1;
    private JTextField txtNickname;
    private JPanel vSpacer1;
    private JProgressBar pgbLoading;
    private JButton btnLogin;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
