/*
 * Created by JFormDesigner on Thu Jan 13 13:21:27 ICT 2022
 */

package client.view.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.*;
import javax.swing.border.*;

import client.StartClient;
import client.view.guiEnums.MainMenuStatus;
import shared.utils.Countdown;

/**
 * @author Khoi Ha
 */
public class MainMenu extends JFrame {

    Countdown acceptPairUpTimer;
    Countdown waitingPairUpTimer;
    final int acceptWaitingTime = 15;

    boolean isAcceptingPairUp = false;

    public MainMenu() {
        initComponents();
        setTitle("Màn hình chính _ Nickname của bạn: " + StartClient.clientSocketHandler.getNickname());
        //Event logout khi tắt window
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                StartClient.clientSocketHandler.logout();
            }
        });

        setDisplayState(MainMenuStatus.DEFAULT);
    }

    private void btnLogout(ActionEvent e) {
        StartClient.clientSocketHandler.logout();
    }

    private void btnCancelPairUp(ActionEvent e) {
        StartClient.clientSocketHandler.cancelPairUp();
    }

    private void btnDecline(ActionEvent e) {
        StartClient.clientSocketHandler.declinePairUp();
    }

    private void btnAccept(ActionEvent e) {
        setDisplayState(MainMenuStatus.WAITING_PARTNER_ACCEPT);
        StartClient.clientSocketHandler.acceptPairUp();
    }

    private void btnPairUp(ActionEvent e) {
        StartClient.clientSocketHandler.pairUp();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Khoi Ha
        pnlLogout = new JPanel();
        btnLogout = new JButton();
        pnlHeader = new JPanel();
        lblTitle = new JLabel();
        pnlWaiting = new JPanel();
        lblWaiting = new JLabel();
        pgbLoading = new JProgressBar();
        btnCancelPairUp = new JButton();
        pnlPartnerFound = new JPanel();
        btnDecline = new JButton();
        btnAccept = new JButton();
        lblFoundPartner = new JLabel();
        lblPairUpCountdown = new JLabel();
        pnlPairUp = new JPanel();
        btnPairUp = new JButton();

        //======== this ========
        setMinimumSize(new Dimension(450, 420));
        var contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        //======== pnlLogout ========
        {
            pnlLogout.setPreferredSize(new Dimension(95, 50));
            pnlLogout.setMinimumSize(new Dimension(95, 20));
            pnlLogout.setBackground(new Color(51, 51, 51));
            pnlLogout.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(new javax.swing.
            border.EmptyBorder(0,0,0,0), "JF\u006frm\u0044es\u0069gn\u0065r \u0045va\u006cua\u0074io\u006e",javax.swing.border.TitledBorder.CENTER
            ,javax.swing.border.TitledBorder.BOTTOM,new java.awt.Font("D\u0069al\u006fg",java.awt.Font
            .BOLD,12),java.awt.Color.red),pnlLogout. getBorder()));pnlLogout. addPropertyChangeListener(
            new java.beans.PropertyChangeListener(){@Override public void propertyChange(java.beans.PropertyChangeEvent e){if("\u0062or\u0064er"
            .equals(e.getPropertyName()))throw new RuntimeException();}});
            pnlLogout.setLayout(null);

            //---- btnLogout ----
            btnLogout.setText("Tho\u00e1t");
            btnLogout.setBorder(new MatteBorder(1, 1, 1, 1, Color.lightGray));
            btnLogout.setBorderPainted(false);
            btnLogout.setBackground(Color.white);
            btnLogout.setFocusPainted(false);
            btnLogout.addActionListener(e -> btnLogout(e));
            pnlLogout.add(btnLogout);
            btnLogout.setBounds(15, 15, 80, 30);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < pnlLogout.getComponentCount(); i++) {
                    Rectangle bounds = pnlLogout.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = pnlLogout.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                pnlLogout.setMinimumSize(preferredSize);
                pnlLogout.setPreferredSize(preferredSize);
            }
        }
        contentPane.add(pnlLogout);

        //======== pnlHeader ========
        {
            pnlHeader.setPreferredSize(new Dimension(0, 50));
            pnlHeader.setBackground(new Color(51, 51, 51));
            pnlHeader.setMinimumSize(new Dimension(450, 30));
            pnlHeader.setLayout(null);

            //---- lblTitle ----
            lblTitle.setText("Chat v\u1edbi ng\u01b0\u1eddi l\u1ea1");
            lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
            lblTitle.setForeground(new Color(204, 204, 204));
            lblTitle.setFont(new Font("Tahoma", Font.BOLD, 16));
            pnlHeader.add(lblTitle);
            lblTitle.setBounds(0, 10, 450, 30);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < pnlHeader.getComponentCount(); i++) {
                    Rectangle bounds = pnlHeader.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = pnlHeader.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                pnlHeader.setMinimumSize(preferredSize);
                pnlHeader.setPreferredSize(preferredSize);
            }
        }
        contentPane.add(pnlHeader);

        //======== pnlWaiting ========
        {
            pnlWaiting.setPreferredSize(new Dimension(0, 160));
            pnlWaiting.setBackground(new Color(51, 51, 51));
            pnlWaiting.setLayout(null);

            //---- lblWaiting ----
            lblWaiting.setText("\u0110ang t\u00ecm ki\u1ebfm...");
            lblWaiting.setForeground(Color.white);
            lblWaiting.setHorizontalAlignment(SwingConstants.CENTER);
            lblWaiting.setFont(new Font("Tahoma", Font.BOLD, 14));
            pnlWaiting.add(lblWaiting);
            lblWaiting.setBounds(0, 15, 450, 42);

            //---- pgbLoading ----
            pgbLoading.setVisible(false);
            pnlWaiting.add(pgbLoading);
            pgbLoading.setBounds(new Rectangle(new Point(150, 70), pgbLoading.getPreferredSize()));

            //---- btnCancelPairUp ----
            btnCancelPairUp.setText("H\u1ee7y");
            btnCancelPairUp.setBackground(Color.white);
            btnCancelPairUp.setBorder(null);
            btnCancelPairUp.setBorderPainted(false);
            btnCancelPairUp.setFocusPainted(false);
            btnCancelPairUp.addActionListener(e -> btnCancelPairUp(e));
            pnlWaiting.add(btnCancelPairUp);
            btnCancelPairUp.setBounds(175, 95, 100, 25);
        }
        contentPane.add(pnlWaiting);

        //======== pnlPartnerFound ========
        {
            pnlPartnerFound.setMinimumSize(new Dimension(0, 20));
            pnlPartnerFound.setPreferredSize(new Dimension(0, 120));
            pnlPartnerFound.setBackground(new Color(51, 51, 51));
            pnlPartnerFound.setLayout(null);

            //---- btnDecline ----
            btnDecline.setText("T\u1eeb ch\u1ed1i");
            btnDecline.setBackground(new Color(255, 0, 51));
            btnDecline.setForeground(Color.lightGray);
            btnDecline.setBorder(null);
            btnDecline.setBorderPainted(false);
            btnDecline.setFocusPainted(false);
            btnDecline.addActionListener(e -> btnDecline(e));
            pnlPartnerFound.add(btnDecline);
            btnDecline.setBounds(125, 65, 85, 30);

            //---- btnAccept ----
            btnAccept.setText("Ch\u1ea5p nh\u1eadn");
            btnAccept.setBackground(new Color(51, 153, 0));
            btnAccept.setForeground(new Color(204, 204, 204));
            btnAccept.setBorder(null);
            btnAccept.setBorderPainted(false);
            btnAccept.setFocusPainted(false);
            btnAccept.addActionListener(e -> btnAccept(e));
            pnlPartnerFound.add(btnAccept);
            btnAccept.setBounds(245, 65, 95, 30);

            //---- lblFoundPartner ----
            lblFoundPartner.setText("B\u1eaft \u0111\u1ea7u chat ?");
            lblFoundPartner.setForeground(Color.white);
            lblFoundPartner.setHorizontalAlignment(SwingConstants.CENTER);
            lblFoundPartner.setFont(new Font("Tahoma", Font.BOLD, 14));
            pnlPartnerFound.add(lblFoundPartner);
            lblFoundPartner.setBounds(0, 0, 450, 17);

            //---- lblPairUpCountdown ----
            lblPairUpCountdown.setText("20s");
            lblPairUpCountdown.setForeground(Color.white);
            lblPairUpCountdown.setHorizontalAlignment(SwingConstants.CENTER);
            pnlPartnerFound.add(lblPairUpCountdown);
            lblPairUpCountdown.setBounds(205, 25, 35, 25);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < pnlPartnerFound.getComponentCount(); i++) {
                    Rectangle bounds = pnlPartnerFound.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = pnlPartnerFound.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                pnlPartnerFound.setMinimumSize(preferredSize);
                pnlPartnerFound.setPreferredSize(preferredSize);
            }
        }
        contentPane.add(pnlPartnerFound);

        //======== pnlPairUp ========
        {
            pnlPairUp.setPreferredSize(new Dimension(0, 40));
            pnlPairUp.setBackground(new Color(51, 51, 51));
            pnlPairUp.setLayout(null);

            //---- btnPairUp ----
            btnPairUp.setText("Gh\u00e9p c\u1eb7p");
            btnPairUp.setBorderPainted(false);
            btnPairUp.setBorder(null);
            btnPairUp.setBackground(Color.white);
            btnPairUp.setFocusPainted(false);
            btnPairUp.addActionListener(e -> btnPairUp(e));
            pnlPairUp.add(btnPairUp);
            btnPairUp.setBounds(175, 5, 105, 30);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < pnlPairUp.getComponentCount(); i++) {
                    Rectangle bounds = pnlPairUp.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = pnlPairUp.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                pnlPairUp.setMinimumSize(preferredSize);
                pnlPairUp.setPreferredSize(preferredSize);
            }
        }
        contentPane.add(pnlPairUp);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void showAllComponents() {
        btnLogout.setEnabled(true);
        pnlWaiting.setVisible(true);
        pnlPartnerFound.setVisible(true);
        btnAccept.setEnabled(true);
        btnDecline.setEnabled(true);
        btnCancelPairUp.setEnabled(true);
        btnPairUp.setEnabled(true);
    }

    public void setDisplayState(MainMenuStatus state) {
        // hiển thị tất cả components
        showAllComponents();

        // ẩn các components tùy theo state
        switch (state) {
            case DEFAULT:
                stopWaitingPairUpTimer();
                stopAcceptPairUpTimer();
                pnlWaiting.setVisible(false);
                pnlPartnerFound.setVisible(false);
                break;

            case FINDING_PARTNER:
                startWaitingPairUpTimer();
                stopAcceptPairUpTimer();
                pnlPartnerFound.setVisible(false);
                btnPairUp.setEnabled(false);
                btnLogout.setEnabled(false);
                break;

            case WAITING_ACCEPT:
                stopWaitingPairUpTimer();
                startAcceptPairUpTimer();
                isAcceptingPairUp = false;
                pnlWaiting.setVisible(false);
                btnPairUp.setEnabled(false);
                btnLogout.setEnabled(false);
                break;

            case WAITING_PARTNER_ACCEPT:
                isAcceptingPairUp = true;
                pnlWaiting.setVisible(false);
                btnAccept.setEnabled(false);
                btnDecline.setEnabled(false);
                btnPairUp.setEnabled(false);
                btnLogout.setEnabled(false);
                lblWaiting.setText("Đang chờ phản hồi từ đối phương..");
                break;
        }
    }

    private void startAcceptPairUpTimer() {
        acceptPairUpTimer = new Countdown(acceptWaitingTime);
        acceptPairUpTimer.setTimerCallBack(
                // end callback
                (Callable) () -> {
                    // reset acceptPairMatchTimer
                    acceptPairUpTimer.restart();
                    acceptPairUpTimer.pause();

                    // automatically decline if the time has passed without accepting
                    if (!isAcceptingPairUp) {
                        StartClient.clientSocketHandler.declinePairUp();
                    }
                    return null;
                },
                // tick callback
                (Callable) () -> {
                    lblPairUpCountdown.setText(acceptPairUpTimer.getCurrentTick() + " s");
                    return null;
                },
                // tick interval
                1
        );
    }

    public void stopAcceptPairUpTimer() {
        if (acceptPairUpTimer != null) {
            acceptPairUpTimer.cancel();
        }
    }

    private void startWaitingPairUpTimer() {
        waitingPairUpTimer = new Countdown(2 * 60); // 2 phút
        AtomicInteger dotCount = new AtomicInteger(1);
        AtomicReference<String> dot = new AtomicReference<>(".");
        waitingPairUpTimer.setTimerCallBack(
                (Callable) () -> {
                    setDisplayState(MainMenuStatus.DEFAULT);
                    JOptionPane.showMessageDialog(this, "Buồn ! Không có ai để chat cùng cả");
                    return null;
                },
                (Callable) () -> {
                    int waitingTime = waitingPairUpTimer.getTimeLimit() - waitingPairUpTimer.getCurrentTick();
                    int mins = waitingTime / 60;
                    int secs = waitingTime % 60;
                    if (dotCount.get() == 1)
                        dot.set(".");
                    else if (dotCount.get() == 2)
                        dot.set("..");
                    else if (dotCount.get() == 3)
                        dot.set("...");
                    else{
                        dotCount.set(1);
                        dot.set(".");
                    }


                    lblWaiting.setText("<html><body style='text-align:center;'>Đang tìm người để chat " + dot +"<br>" +
                            (mins < 10 ? "0" + mins : mins) + ":" + (secs < 10 ? "0" + secs : secs) + "</body></html>");
                    dotCount.getAndIncrement();
                    return null;
                },
                1
        );
    }

    public void stopWaitingPairUpTimer() {
        if (waitingPairUpTimer != null) {
            waitingPairUpTimer.cancel();
        }
    }

    public void foundPartner(String partnerNickname) {
        setDisplayState(MainMenuStatus.WAITING_ACCEPT);
        lblFoundPartner.setText("Ghép cặp với " + partnerNickname + "?");
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Khoi Ha
    private JPanel pnlLogout;
    private JButton btnLogout;
    private JPanel pnlHeader;
    private JLabel lblTitle;
    private JPanel pnlWaiting;
    private JLabel lblWaiting;
    private JProgressBar pgbLoading;
    private JButton btnCancelPairUp;
    private JPanel pnlPartnerFound;
    private JButton btnDecline;
    private JButton btnAccept;
    private JLabel lblFoundPartner;
    private JLabel lblPairUpCountdown;
    private JPanel pnlPairUp;
    private JButton btnPairUp;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
