package com.emc.kazeon.connector.opentext;

import com.emc.kazeon.connector.websvc.trees.BaseObject;

public class OpentextPath {

	public final boolean isRoot;
	public final boolean isWorkSpace;
	public final String workSpaceName;
	//filePathArray is the path to the file from workspace, workspace not included.
	public final String[] filePathArray; 
	
	public OpentextPath(BaseObject bo) {
		
		if(BaseObject.isRoot(bo)){
			this.isRoot = true;
			this.isWorkSpace = false;
			this.workSpaceName = null;
			this.filePathArray = null;
		}
		else{
			this.isRoot = false;
			String[] kazpathArray = bo.pathComponents.clone();
			if(kazpathArray.length < 1) throw new IllegalArgumentException(bo.kazPath);
			
			//Path Array length=1 means there is only one path element
			//then it should be workspace.
			if(kazpathArray.length == 1){
				this.isWorkSpace = true;
				this.workSpaceName = kazpathArray[0];
				this.filePathArray = null;
			}else{
				// Proper data repository path
				this.isWorkSpace = false;
				this.workSpaceName = kazpathArray[0];
				this.filePathArray = new String[kazpathArray.length-1];
				for(int i=1;i<kazpathArray.length;i++){
					filePathArray[i-1] = kazpathArray[i];
				}
			}	
		}
	}
	
	@Override
	public String toString() {
		String tmpString = "Root :"+this.isRoot+" ,isWorkSpace :"+this.isWorkSpace+ " ,workSpaceName: "+this.workSpaceName;
		StringBuilder pathString = new StringBuilder();
		if(filePathArray != null){
			for(int i=0;i<this.filePathArray.length;i++){
				pathString.append("/"+this.filePathArray[i]);
			}
		}
		return tmpString+" Path-String :"+pathString.toString();
	}
	
	public static void main(String[] args) {
		//String kazpath = "/enterpriseWS/Aditya/sebin/hello.txt";
		String kazpath = "/enterpriseWS/Aditya/sebin/hello.txt";
		BaseObject bo = BaseObject.buildPartialBO(kazpath);
		OpentextPath path = new OpentextPath(bo);
		System.out.println(path.toString());
	}
	
}
