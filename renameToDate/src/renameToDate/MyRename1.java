package renameToDate;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JTextArea;

import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import javax.swing.JScrollPane;
import javax.swing.JCheckBox;

public class MyRename1 extends JFrame {

	private static final long serialVersionUID = 1L;
	static final String SURFIX=".jpg";
	static String newName;
	static File newFile;
	int count_exif=0;
	int count_modify=0;
	private JPanel contentPane;
	private JTextField textField;
	private JButton button = new JButton("Rename");
	private final JScrollPane scrollPane = new JScrollPane();
	private final JTextArea textArea = new JTextArea();
    private javax.swing.JCheckBox chckbxNewCheckBox;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MyRename1 frame = new MyRename1();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MyRename1() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 410);
        setTitle("Rename to Datetime");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		textField = new JTextField();
		textField.setToolTipText("image folder path");
		textField.setDragEnabled(true);
		textField.setColumns(10);
		
		chckbxNewCheckBox = new JCheckBox("Check this to rename files without EXIF to modify time");
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
							.addContainerGap())
						.addComponent(chckbxNewCheckBox, GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addComponent(textField, GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
							.addGap(18)
							.addComponent(button, GroupLayout.PREFERRED_SIZE, 146, GroupLayout.PREFERRED_SIZE)
							.addGap(20))))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(button, GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxNewCheckBox)
					.addGap(6)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		scrollPane.setViewportView(textArea);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File dir=new File(textField.getText());
				if(!dir.isDirectory()){
					textArea.append("Wrong image folder path \n or path is not a folder\n");
		        } else {
		        	textArea.setText("");
		        	File[] s=dir.listFiles(new FilenameFilter() {

		                public boolean accept(File file, String name) {
		                	name=name.toLowerCase();
		                    return name.endsWith("jpg") || name.endsWith("jpeg");
		                }
		            });
		        	ArrayList<File> list = new ArrayList<File>();
		        	for (File f:s){
		    			try {
		    	            Metadata metadata = JpegMetadataReader.readMetadata(f);
		    	            ExifSubIFDDirectory directory = metadata.getDirectory(ExifSubIFDDirectory.class);
		    	            Date myDate = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
		    	            newName=new SimpleDateFormat("yyMMdd-HHmmss").format(myDate).toString();
		    	            newFile=new File(f.getParent(), newName+SURFIX);
		    	            int i=0;
		    	            while(newFile.exists()){
		    	            	i++;
		    	            	newFile=new File(newFile.getParent(), newName+i+SURFIX);
		    	            }		    	            
		    	            textArea.append(f.renameTo(newFile)+"\t"+f.getAbsolutePath()+"\n");
		    	            count_exif++;
		    	        } catch (ImageProcessingException e) {
		    	            System.err.println("error 1a: " + e);
		    	        } catch (IOException e) {
		    	            System.err.println("error 1b: " + e);
		    	        } catch (NullPointerException e) {
		    	        	list.add(f);
		    	        }
		    		}
		        	if (!(list.isEmpty()) && (chckbxNewCheckBox.isSelected())){
		        		textArea.append("\n\n\n\nrenamed to last modify time:\n");
		        		DateFormat format = new SimpleDateFormat("yyMMdd-HHmmss");
		        		File f1;
		        		for (int i = 0; i < list.size(); i++) {
		        			f1= list.get(i);
		        			newName=format.format(new Date(f1.lastModified()));
		        			newFile=new File(f1.getParent(), newName+SURFIX);
		    	            int ii=0;
		    	            while(newFile.exists()){
		    	            	ii++;
		    	            	newFile=new File(newFile.getParent(), newName+ii+SURFIX);
		    	            }
		    	            textArea.append(f1.renameTo(newFile)+"\t"+f1.getAbsolutePath()+"\n");
		    	            count_modify++;
		        		}
		        		textArea.append(count_exif+"\t files renamed to EXIF datetime. \n");
		        		textArea.append(count_modify+"\t files renamed to last modifed time. \n");
		        		list.clear();
		        		count_modify=0;
		        		count_exif=0;
		        	} else {
		        		textArea.append(count_exif+"\t files renamed to EXIF datetime. \n");
		        		count_exif=0;
		        	}
		        }
			}
		});
		contentPane.setLayout(gl_contentPane);
	}
}
