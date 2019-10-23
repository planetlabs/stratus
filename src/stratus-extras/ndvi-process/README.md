# geotools-ndvi

## NDVI process for GeoTools

Adds a NDVI WPS process for use with GeoServer/GeoTools WPS or styling.

## Usage

NDVI process has two parameters

- `redBand`: Index of the red band. Default is 0.
- `nirBand`: Index of the NIR band. Default is 3.

If your sample model does not match the defaults you will need to provide these parameters. GeoTools cannot tell
which bands are which otherwise. 

### In SLD
```xml
<?xml version="1.0" encoding="ISO-8859-1"?>
   <StyledLayerDescriptor version="1.0.0"
       xsi:schemaLocation="http://www.opengis.net/sld StyledLayerDescriptor.xsd"
       xmlns="http://www.opengis.net/sld"
       xmlns:ogc="http://www.opengis.net/ogc"
       xmlns:xlink="http://www.w3.org/1999/xlink"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
     <NamedLayer>
       <Name>NDVI</Name>
       <UserStyle>
         <Title>NDVI</Title>
         <FeatureTypeStyle>
           <Transformation>
             <ogc:Function name="ras:NDVI">
               <ogc:Function name="parameter">
                 <ogc:Literal>coverage</ogc:Literal>
               </ogc:Function>
               <ogc:Function name="parameter">
                 <ogc:Literal>redBand</ogc:Literal>
                 <ogc:Literal>0</ogc:Literal>
               </ogc:Function>
               <ogc:Function name="parameter">
                 <ogc:Literal>nirBand</ogc:Literal>
                 <ogc:Literal>3</ogc:Literal>
               </ogc:Function>
             </ogc:Function>
           </Transformation>
          <Rule>
            <RasterSymbolizer>
              <ColorMap type="ramp" >
                <ColorMapEntry color="#000000" quantity="-1"/>
                <ColorMapEntry color="#d7191c" quantity="0.00698"/>
                <ColorMapEntry color="#fdae61" quantity="0.0363"/>
                <ColorMapEntry color="#ffffc0" quantity="0.0656" />
                <ColorMapEntry color="#a6d96a" quantity="0.0949" />
                <ColorMapEntry color="#1a9641" quantity="0.124" />
              </ColorMap>
            </RasterSymbolizer>
           </Rule>
         </FeatureTypeStyle>
       </UserStyle>
     </NamedLayer>
    </StyledLayerDescriptor>
```

