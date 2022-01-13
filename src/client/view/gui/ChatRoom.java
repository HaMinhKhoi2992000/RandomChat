/*
 * Created by JFormDesigner on Thu Jan 13 14:45:43 ICT 2022
 */

package client.view.gui;

import java.awt.event.*;
import javax.swing.border.*;
import client.StartClient;
import shared.model.Message;

import java.awt.*;
import java.io.IOException;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;

/**
 * @author Khoi Ha
 */

public class ChatRoom extends JFrame {
    private String you;
    private String partner;
    private HTMLDocument doc;

    public ChatRoom() {
        initComponents();
        txtChatArea.setContentType( "text/html" );
        setTitle("Phòng trò chuyện - Nickname của bạn " + StartClient.clientSocketHandler.getNickname());

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(ChatRoom.this,
                        "Xác nhận thoát phòng ?", "Thoát phòng?",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    StartClient.clientSocketHandler.leaveChatRoom();
                }
            }
        });

        doc = (HTMLDocument) txtChatArea.getStyledDocument();
        txtChatArea.setText("<br/>");

        // Generate new line of txtMessage on CTRL + ENTER
        InputMap input = txtInput.getInputMap();
        KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
        KeyStroke controlEnter = KeyStroke.getKeyStroke("shift ENTER");
        input.put(controlEnter, "insert-break");
        input.put(enter, "text-submit");

        ActionMap actions = txtInput.getActionMap();
        actions.put("text-submit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String content = txtInput.getText();
                sendMessage(content);
            }
        });
    }

    public static void main(String[] args) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setVisible(true);
    }

    public void addChatMessage(Message message) {
        try {
            doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                    "<div style='background-color: #ebebeb; margin: 0 0 10px 0;'><pre style='color: #000;'>"
                            + "<span style='color: red;'>" + message.getSender() + ": </span>" + message.getContent() + "</pre></div><br/>");
        } catch (BadLocationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        txtChatArea.setCaretPosition(txtChatArea.getDocument().getLength());
    }

    private void sendMessage(String content) {
        if(!content.equals("")) {
            Message message = new Message(you, partner, content);
            txtInput.setText("");
            StartClient.clientSocketHandler.sendChatMessage(message);


            try {
                doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                        "<div style='background-color: #6c7a89; margin: 0 0 10px 0;'><pre style='color: #fff'>"
                                + "<span style='color: grey;'>Bạn: </span>" + content + "</pre></div><br/>");
            } catch (BadLocationException | IOException badLocationException) {
                badLocationException.printStackTrace();
            }

            txtChatArea.setCaretPosition(txtChatArea.getDocument().getLength());
        }
    }

    public void setClients(String you, String partner) {
        this.partner = partner;
        this.you = you;
        this.lblTitle.setText("Trò chuyện với " + partner);
    }

    private void btnSend(ActionEvent e) {
        String content = txtInput.getText();
        sendMessage(content);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Khoi Ha
        pnlBottom = new JPanel();
        btnSend = new JButton();
        scrollPane1 = new JScrollPane();
        txtInput = new JTextArea();
        pnHeader = new JPanel();
        lblTitle = new JLabel();
        pnlBody = new JPanel();
        scrollPane2 = new JScrollPane();
        txtChatArea = new JTextPane();

        //======== this ========
        setMinimumSize(new Dimension(530, 390));
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== pnlBottom ========
        {
            pnlBottom.setMinimumSize(new Dimension(0, 40));
            pnlBottom.setPreferredSize(new Dimension(0, 40));
            pnlBottom.setBorder ( new javax . swing. border .CompoundBorder ( new javax . swing. border .TitledBorder ( new javax
            . swing. border .EmptyBorder ( 0, 0 ,0 , 0) ,  "JF\u006frmDes\u0069gner \u0045valua\u0074ion" , javax. swing
            .border . TitledBorder. CENTER ,javax . swing. border .TitledBorder . BOTTOM, new java. awt .
            Font ( "D\u0069alog", java .awt . Font. BOLD ,12 ) ,java . awt. Color .red
            ) ,pnlBottom. getBorder () ) ); pnlBottom. addPropertyChangeListener( new java. beans .PropertyChangeListener ( ){ @Override
            public void propertyChange (java . beans. PropertyChangeEvent e) { if( "\u0062order" .equals ( e. getPropertyName (
            ) ) )throw new RuntimeException( ) ;} } );
            pnlBottom.setLayout(null);

            //---- btnSend ----
            btnSend.setText("G\u1eedi");
            btnSend.setPreferredSize(new Dimension(80, 40));
            btnSend.setMinimumSize(new Dimension(56, 40));
            btnSend.setMaximumSize(new Dimension(56, 40));
            btnSend.setBorderPainted(false);
            btnSend.setFocusPainted(false);
            btnSend.setBorder(null);
            btnSend.setBackground(new Color(51, 51, 51));
            btnSend.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
            btnSend.setForeground(Color.white);
            btnSend.addActionListener(e -> btnSend(e));
            pnlBottom.add(btnSend);
            btnSend.setBounds(new Rectangle(new Point(448, 0), btnSend.getPreferredSize()));

            //======== scrollPane1 ========
            {

                //---- txtInput ----
                txtInput.setPreferredSize(new Dimension(1, 40));
                txtInput.setMinimumSize(new Dimension(1, 40));
                txtInput.setBackground(Color.darkGray);
                txtInput.setForeground(Color.white);
                txtInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
                scrollPane1.setViewportView(txtInput);
            }
            pnlBottom.add(scrollPane1);
            scrollPane1.setBounds(0, 0, 448, 40);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < pnlBottom.getComponentCount(); i++) {
                    Rectangle bounds = pnlBottom.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = pnlBottom.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                pnlBottom.setMinimumSize(preferredSize);
                pnlBottom.setPreferredSize(preferredSize);
            }
        }
        contentPane.add(pnlBottom, BorderLayout.SOUTH);

        //======== pnHeader ========
        {
            pnHeader.setPreferredSize(new Dimension(0, 40));
            pnHeader.setBackground(new Color(51, 51, 51));
            pnHeader.setForeground(Color.white);
            pnHeader.setLayout(null);

            //---- lblTitle ----
            lblTitle.setText("Cu\u1ed9c tr\u00f2 chuy\u1ec7n");
            lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
            lblTitle.setFont(new Font("Tahoma", Font.BOLD, 22));
            lblTitle.setForeground(Color.white);
            pnHeader.add(lblTitle);
            lblTitle.setBounds(5, 10, 520, lblTitle.getPreferredSize().height);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < pnHeader.getComponentCount(); i++) {
                    Rectangle bounds = pnHeader.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = pnHeader.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                pnHeader.setMinimumSize(preferredSize);
                pnHeader.setPreferredSize(preferredSize);
            }
        }
        contentPane.add(pnHeader, BorderLayout.NORTH);

        //======== pnlBody ========
        {
            pnlBody.setPreferredSize(new Dimension(10, 100));
            pnlBody.setMinimumSize(new Dimension(22, 100));
            pnlBody.setLayout(new BorderLayout());

            //======== scrollPane2 ========
            {

                //---- txtChatArea ----
                txtChatArea.setEditable(false);
                txtChatArea.setBorder(new MatteBorder(2, 2, 2, 2, Color.gray));
                scrollPane2.setViewportView(txtChatArea);
            }
            pnlBody.add(scrollPane2, BorderLayout.CENTER);
        }
        contentPane.add(pnlBody, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Khoi Ha
    private JPanel pnlBottom;
    private JButton btnSend;
    private JScrollPane scrollPane1;
    private JTextArea txtInput;
    private JPanel pnHeader;
    private JLabel lblTitle;
    private JPanel pnlBody;
    private JScrollPane scrollPane2;
    private JTextPane txtChatArea;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
